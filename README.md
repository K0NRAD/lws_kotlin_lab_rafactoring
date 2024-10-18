# Refactoring einer Artikel-Verwaltung nach dem MVC-Pattern

Heute begeben wir uns auf eine spannende Reise in die Welt des Refactorings. Stellt euch vor, wir haben ein altes Haus (
unseren Code) und wollen es renovieren, ohne es komplett abzureißen. Das nennen wir Refactoring! Unser Ziel ist es, eine
einfache Artikel-Verwaltung in Kotlin nach dem MVC-Pattern umzustrukturieren.

## Was ist das MVC-Pattern?

Bevor wir loslegen, lasst uns kurz über das MVC-Pattern sprechen. MVC steht für:

- Model: Unsere Daten und Geschäftslogik
- View: Die Darstellung für den Benutzer
- Controller: Der Vermittler zwischen Model und View

Stellt euch das wie ein Restaurant vor:

- Model: Die Küche mit den Rezepten und Zutaten
- View: Der Speisesaal, wo die Gäste sitzen
- Controller: Die Kellner, die Bestellungen aufnehmen und Essen servieren

## Schritt 1: Identifizierung der Komponenten

> ***WICHTIG:*** Der folgende Code dient nur der Orientierung, er zeigt grob wie du vorgehen kannst, um die Anwendung nach
dem MVC Pattern refaktorieren.

Lasst uns zuerst unseren bestehenden Code analysieren und die Teile identifizieren, die zu Model, View und Controller
gehören könnten:

### Model:

```kotlin
data class Article(val id: String, val name: String, val price: Double, val eans: List<String>)
```

### View (aktuell noch im Hauptprogramm):

- Menüanzeige
- Eingabeaufforderungen
- Ausgabe von Artikellisten

### Controller (aktuell noch im Hauptprogramm):

- Logik für das Hinzufügen, Ändern, Suchen und Löschen von Artikeln

## Schritt 2: Erstellung der Model-Klasse

Wir haben bereits eine gute Grundlage mit unserer `Article`-Klasse. Lass uns diese in eine separate Datei verschieben:

```kotlin
// Article.kt
package schwarz.it.lws.articlemanagement.model

data class Article(val id: String, val name: String, val price: Double, val eans: List<String>) {
    companion object {
        fun headers(): String {
            return "%-10s | %-12s | %-8s | %-13s".format("ID", "NAME", "PREIS", "EANS")
        }
    }

    override fun toString(): String {
        val str = "%10s | %12s | %8.2f | ".format(id, name, price)
        return str + eans.map { "%13s".format(it) }.joinToString(" | ")
    }
}
```

So, das war der erste Schritt! Wir haben unser Model sauber in eine eigene Datei gepackt. Das ist wie wenn du in deinem
Zimmer alle Spielsachen in eine Kiste räumst - alles ist am richtigen Platz und leicht zu finden.

## Schritt 3: Erstellung der View-Klasse

Jetzt kümmern wir uns um die Darstellung. Die View ist wie das Schaufenster unseres Ladens - sie zeigt alles schön an,
ohne sich um die Logik dahinter zu kümmern.

```kotlin
// ArticleView.kt
package schwarz.it.lws.articlemanagement.view

import schwarz.it.lws.articlemanagement.model.Article

class ArticleView {
    fun displayMenu() {
        println(
            """
            
            --- Artikel Verwaltung ---
              1 - Artikel anlegen
              2 - Artikel auflisten
              3 - Artikel ändern
              4 - Artikel suchen
              5 - Artikel löschen
              0 - Programm beenden
        """.trimIndent()
        )
    }

    fun getInput(prompt: String): String {
        print("$prompt: ")
        return readln().trim()
    }

    fun displayArticles(articles: List<Article>) {
        println(Article.headers())
        for (article in articles.sortedBy { it.id }) {
            println(article)
        }
    }

    fun displayMessage(message: String) {
        println(message)
    }
}
```

In dieser View-Klasse haben wir alle Ausgaben und Eingaben zusammengefasst. Das ist, als würden wir alle Schilder und
Formulare in unserem Laden an einem Ort aufbewahren.

## Schritt 4: Erstellung der Controller-Klasse

Der Controller ist das Herzstück unserer Anwendung. Er ist wie der Ladenbesitzer, der alles koordiniert.

```kotlin
// ArticleController.kt
package schwarz.it.lws.articlemanagement.controller

import schwarz.it.lws.articlemanagement.model.Article
import schwarz.it.lws.articlemanagement.view.ArticleView

class ArticleController(private val view: ArticleView) {
    private val articles = mutableListOf<Article>()

    fun run() {
        var running = true
        while (running) {
            view.displayMenu()
            when (view.getInput("wählen >")) {
                "0" -> running = false
                "1" -> addArticle()
                "2" -> listArticles()
                "3" -> modifyArticle()
                "4" -> searchArticle()
                "5" -> deleteArticle()
                else -> view.displayMessage("Falscheingabe: Nur Zahlen zwischen 0 und 5 erlaubt.")
            }
        }
    }

    private fun addArticle() {
        val id = view.getInput("ID")
        if (articles.any { it.id == id }) {
            view.displayMessage("Falscheingabe: ID schon vorhanden.")
            return
        }
        val name = view.getInput("NAME")
        val price = view.getInput("PREIS").toDoubleOrNull() ?: run {
            view.displayMessage("Falscheingabe: Nur Zahlen erlaubt.")
            return
        }
        val eans = view.getInput("EANS").split(',')
        articles.add(Article(id, name, price, eans))
    }

    private fun listArticles() {
        view.displayArticles(articles)
    }

    // Hier würden die Methoden für modifyArticle(), searchArticle() und deleteArticle() folgen
    // Sie würden ähnlich wie addArticle() aufgebaut sein, mit Eingaben über die View
    // und Operationen auf der articles-Liste
}
```

Dieser Controller übernimmt die gesamte Logik unserer Anwendung. Er entscheidet, was passiert, wenn der Benutzer eine
Eingabe macht, und steuert den Datenfluss zwischen Model und View.

So, das war eine Menge Input! Wir haben jetzt die grundlegende Struktur für unser MVC-Pattern geschaffen. Jeder Teil hat
seine eigene Aufgabe, genau wie in einem gut organisierten Team.

## Schritt 5: Vervollständigung des Controllers

Lass uns die fehlenden Methoden im Controller ergänzen. Diese Methoden sind wie verschiedene Abteilungen in unserem
Laden - jede hat ihre spezielle Aufgabe.

```kotlin
// ArticleController.kt (Fortsetzung)

class ArticleController(private val view: ArticleView) {
    // ... (vorherige Methoden bleiben unverändert)

    private fun modifyArticle() {
        val id = view.getInput("ID")
        val article = articles.find { it.id == id }
        if (article == null) {
            view.displayMessage("Falscheingabe: Artikel mit ID $id nicht vorhanden.")
            return
        }

        val name = view.getInput("NAME").takeIf { it.isNotEmpty() } ?: article.name
        val priceInput = view.getInput("PREIS")
        val price = if (priceInput.isNotEmpty()) priceInput.toDoubleOrNull() ?: article.price else article.price
        val eansInput = view.getInput("EANS")
        val eans = if (eansInput.isNotEmpty()) eansInput.split(',') else article.eans

        articles.remove(article)
        articles.add(Article(id, name, price, eans))
        view.displayMessage("Artikel wurde aktualisiert.")
    }

    private fun searchArticle() {
        view.displayMessage(
            """
            1 - suchen nach ID   
            2 - suchen nach NAME   
            3 - suchen nach PRICE
            4 - suchen nach EAN
            0 - abbrechen
        """.trimIndent()
        )

        when (view.getInput("Wähle eine Option")) {
            "1" -> searchById()
            "2" -> searchByName()
            "3" -> searchByPrice()
            "4" -> searchByEan()
            "0" -> return
            else -> view.displayMessage("Falscheingabe: Nur Zahlen 0 - 4 erlaubt.")
        }
    }

    private fun searchById() {
        val id = view.getInput("ID")
        val foundArticles = articles.filter { it.id == id }
        displaySearchResults(foundArticles)
    }

    private fun searchByName() {
        val name = view.getInput("NAME")
        val foundArticles = articles.filter { it.name == name }
        displaySearchResults(foundArticles)
    }

    private fun searchByPrice() {
        val price = view.getInput("PREIS").toDoubleOrNull()
        if (price == null) {
            view.displayMessage("Falscheingabe: Nur Zahlen erlaubt.")
            return
        }
        val foundArticles = articles.filter { it.price == price }
        displaySearchResults(foundArticles)
    }

    private fun searchByEan() {
        val ean = view.getInput("EAN")
        val foundArticles = articles.filter { it.eans.contains(ean) }
        displaySearchResults(foundArticles)
    }

    private fun displaySearchResults(foundArticles: List<Article>) {
        if (foundArticles.isNotEmpty()) {
            view.displayArticles(foundArticles)
        } else {
            view.displayMessage("Keine Artikel gefunden.")
        }
    }

    private fun deleteArticle() {
        val id = view.getInput("ID")
        val removed = articles.removeIf { it.id == id }
        if (removed) {
            view.displayMessage("Artikel wurde gelöscht.")
        } else {
            view.displayMessage("Falscheingabe: Artikel mit ID $id nicht vorhanden.")
        }
    }
}
```

Diese Methoden vervollständigen unseren Controller. Jede Methode hat eine klare Aufgabe:

- `modifyArticle()` aktualisiert einen bestehenden Artikel
- `searchArticle()` und die zugehörigen Suchmethoden finden Artikel nach verschiedenen Kriterien
- `deleteArticle()` entfernt einen Artikel aus der Liste

Stell dir vor, jede dieser Methoden wäre ein Mitarbeiter in unserem Laden, der eine spezielle Aufgabe hat - einer
kümmert sich um Änderungen, einer um die Suche und einer um das Entfernen von Artikeln.

## Schritt 6: Hauptprogramm aktualisieren

Jetzt, wo wir alle Teile haben, müssen wir nur noch unser Hauptprogramm aktualisieren, um alles zusammenzubringen.

```kotlin
// App.kt
package schwarz.it.lws.articlemanagement

import schwarz.it.lws.articlemanagement.controller.ArticleController
import schwarz.it.lws.articlemanagement.view.ArticleView

fun main() {
    val view = ArticleView()
    val controller = ArticleController(view)
    controller.run()
}
```

Das Hauptprogramm ist jetzt super schlank! Es erstellt einfach die View und den Controller und startet dann die
Anwendung. Das ist wie wenn du morgens den Laden aufschließt und alles zum Laufen bringst - der Rest läuft dann von
selbst.

So, das war eine Menge Arbeit! Wir haben unsere Artikel-Verwaltung komplett nach dem MVC-Pattern umstrukturiert. Jeder
Teil hat jetzt seine eigene, klar definierte Aufgabe.

## Vorteile des MVC-Patterns

1. **Trennung der Zuständigkeiten**
    - Model: Kümmert sich nur um die Daten
    - View: Zeigt die Daten an
    - Controller: Steuert den Datenfluss

   Das ist wie in einer gut organisierten Küche: Der Koch (Model) bereitet das Essen zu, der Kellner (Controller) bringt
   es zum Tisch, und der Teller (View) präsentiert es dem Gast.

2. **Bessere Wartbarkeit**
   Wenn du nur die Darstellung ändern möchtest, musst du nur die View anpassen. Willst du die Geschäftslogik ändern? Nur
   der Controller muss angepasst werden. Das spart Zeit und reduziert Fehler.

3. **Wiederverwendbarkeit**
   Du kannst leicht eine neue Benutzeroberfläche hinzufügen, ohne die Geschäftslogik zu ändern. Stell dir vor, du
   möchtest neben deinem Laden auch einen Online-Shop eröffnen - mit MVC ist das viel einfacher!

4. **Teamarbeit**
   Verschiedene Entwickler können gleichzeitig an verschiedenen Teilen der Anwendung arbeiten, ohne sich gegenseitig in
   die Quere zu kommen.

5. **Testbarkeit**
   Du kannst jeden Teil separat testen. Das ist, als würdest du in deinem Laden jede Abteilung einzeln auf
   Funktionalität prüfen.

## Glosar

1. **Refactoring**
   Die Umstrukturierung von bestehendem Code, um ihn besser lesbar, wartbarer oder effizienter zu machen, ohne seine
   externe Funktionalität zu ändern.

2. **MVC (Model-View-Controller)**
   Ein Architekturmuster, das eine Anwendung in drei miteinander verbundene Komponenten aufteilt.

3. **Model**
   Repräsentiert die Daten und die Geschäftslogik einer Anwendung. In unserem Fall die `Article`-Klasse.

4. **View**
   Zuständig für die Darstellung der Daten und die Benutzerinteraktion. Bei uns die `ArticleView`-Klasse.

5. **Controller**
   Vermittelt zwischen Model und View, steuert den Datenfluss und die Anwendungslogik. Unsere `ArticleController`
   -Klasse.

6. **Kotlin**
   Eine moderne Programmiersprache, die auf der Java Virtual Machine (JVM) läuft und vollständig mit Java kompatibel
   ist.

7. **data class**
   Eine spezielle Klasse in Kotlin, die hauptsächlich dazu dient, Daten zu halten. Sie bietet automatisch einige
   nützliche Funktionen wie `toString()`, `equals()`, und `hashCode()`.

8. **companion object**
   Ein Objekt in Kotlin, das an eine Klasse gebunden ist und ähnlich wie statische Methoden in Java funktioniert.

9. **Methode**
   Eine Funktion, die zu einer Klasse gehört und das Verhalten der Objekte dieser Klasse definiert.

10. **Konstruktor**
    Eine spezielle Methode, die aufgerufen wird, wenn ein neues Objekt einer Klasse erstellt wird.

11. **when-Ausdruck**
    Eine Art Switch-Case in Kotlin, das verschiedene Codepfade basierend auf einem Wert ausführt.

12. **Liste (List)**
    Eine geordnete Sammlung von Elementen in Kotlin. `mutableListOf` erstellt eine veränderbare Liste.

13. **Lambda-Ausdruck**
    Eine anonyme Funktion, die als Ausdruck übergeben werden kann. Beispiel: `articles.sortedBy { it.id }`

14. **Null-Safety**
    Ein Feature in Kotlin, das hilft, Null-Pointer-Exceptions zu vermeiden. Der `?`-Operator wird für nullable Typen
    verwendet.

15. **String-Interpolation**
    Eine Möglichkeit, Variablen direkt in Strings einzubetten. In Kotlin mit `$variableName` oder `${expression}`.

16. **Bedingte Zuweisung (Elvis-Operator)**
    Der `?:` Operator in Kotlin, der einen Standardwert zuweist, wenn der Ausdruck null ist.

17. **Extension Function**
    Eine Funktion, die den Funktionsumfang einer existierenden Klasse erweitert, ohne sie zu verändern.



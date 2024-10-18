package schwarz.it.lws.articlemanagement

val FOREVER = true
val TEST_DATA = true

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

fun String.isNumeric(): Boolean = this.matches("""^-?\d+(\.\d+)?$""".toRegex())
fun String.isInteger(): Boolean = this.matches("""^-?\d+$""".toRegex())
fun Int.isInRange(range: IntRange): Boolean = this in range

fun main() {
    val articles = mutableListOf<Article>()

    if (TEST_DATA) {
        articles.addAll(
            listOf(
                Article("1001", "Apfel grün", 1.99, listOf("1234567890128", "9876543210986")),
                Article("1002", "Apfel rot", 2.99, listOf("5678901234565", "3456789012340")),
                Article("1003", "Banane", 3.49, listOf("7890123456789")),
            )
        )
    }

    var running = FOREVER
    while (running) {
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
        print("wählen >")
        val input = readln().trim()
        if (!input.isInteger()) {
            println("Falscheingabe: Nur Zahlen erlaubt.")
            continue
        }

        val selection = input.toInt()

        if (!selection.isInRange(0..5)) {
            println("Falscheingabe: Nur Zahlen zwischen 0 und 4 erlaubt.")
            continue
        }

        // 0 - App beenden
        if (selection == 0) {
            running = false;
            continue
        }

        // 1 - Artikel anlegen
        if (selection == 1) {
            println("---- Artikel anlegen ----")
            print("ID:")
            val id = readln().trim()
            if (articles.any { it.id == id }) {
                println("Falscheingabe: ID schon vorhanden.")
                continue
            }
            print("NAME:")
            val name = readln().trim()
            var price = 0.0
            while (true) {
                print("PREIS:")
                val input = readln().trim()
                if (input.isNumeric()) {
                    price = input.toDouble()
                    break
                }
                println("Falscheingabe: Nur Zahlen erlaubt.")
            }

            print("EANS:")
            val eans = readln().trim().split(',')
            articles.add(Article(id, name, price, eans))
            continue
        }

        // 2 - Artikel auflisten
        if (selection == 2) {
            println("---- Artikel auflisten ----")
            println(Article.headers())
            for (article in articles.sortedBy { it.id }) {
                println(article)
            }
            continue
        }

        // 3 - Artikel ändern
        if (selection == 3) {
            println("---- Artikel ändern ----")
            print("ID:")
            val id = readln().trim()
            if (!articles.any { it.id == id }) {
                println("Falscheingabe: Artikel mit ID $id nicht vorhanden.")
                continue
            }

            print("NAME:")
            var name = readln().trim()
            var price = ""
            while (true) {
                print("PREIS:")
                val input = readln().trim()
                if (input.isEmpty()) {
                    break
                }
                if (input.isNumeric()) {
                    price = input
                    break
                }
                println("Falscheingabe: Nur Zahlen erlaubt.")
            }

            print("EANS:")
            val input = readln().trim()
            var eans = emptyList<String>()
            if (!input.isEmpty()) {
                eans = input.split(',')
            }
            val article = articles.find { it.id == id }

            name = if (name.isEmpty()) article!!.name else name
            price = if (price.isEmpty()) article!!.price.toString() else price
            eans = if (eans.isEmpty()) article!!.eans else eans

            articles.removeIf { it.id == id }
            articles.add(Article(id, name, price.toDouble(), eans))
            continue
        }

        // Artikel suchen
        if (selection == 4) {
            println("---- Artikel suchen ----")
            println(
                """
                    1 - suchen nach ID   
                    2 - suchen nach NAME   
                    3 - suchen nach PRICE
                    4 - suchen nach EAN
                    0 - abbrechen
            """.trimIndent()
            )
            val input = readln().trim()
            if (!input.isInteger()) {
                println("Falscheingabe: Nur Zahlen 0 - 4 erlaubt.")
                continue
            }
            val selection = input.toInt()
            if (!selection.isInRange(0..4)) {
                println("Falscheingabe: Nur Zahlen 0 - 4 erlaubt.")
                continue
            }
            if (selection == 0) {
                continue
            }
            // suchen nach id
            if (selection == 1) {
                println("ID:")
                val id = readln().trim()
                val foundetArticles = articles.filter { it.id == id }
                if (foundetArticles.isNotEmpty()) {
                    println(Article.headers())
                    for (foundetArticle in foundetArticles.sortedBy { it.id }) {
                        println(foundetArticle)
                    }
                }
            }
            // suchen nach namen
            if (selection == 2) {
                println("NAME:")
                val name = readln().trim()
                val foundetArticles = articles.filter { it.name == name }
                if (foundetArticles.isNotEmpty()) {
                    println(Article.headers())
                    for (foundetArticle in foundetArticles.sortedBy { it.id }) {
                        println(foundetArticle)
                    }
                }
            }
            // suchen nach preis
            if (selection == 3) {
                println("PREIS:")
                val price = readln().trim()
                if (!price.isNumeric()) {
                    println("Falscheingabe: Nur Zahlen erlaubt.")
                    continue
                }
                val foundetArticles = articles.filter { it.price == price.toDouble() }
                if (foundetArticles.isNotEmpty()) {
                    println(Article.headers())
                    for (foundetArticle in foundetArticles.sortedBy { it.id }) {
                        println(foundetArticle)
                    }
                }
            }
            // suchen nach ean
            if (selection == 4) {
                print("EAN:")
                val ean = readln().trim()
                val foundetArticles = articles.filter { it.eans.contains(ean) }
                if (foundetArticles.isNotEmpty()) {
                    println(Article.headers())
                    for (foundetArticle in foundetArticles.sortedBy { it.id }) {
                        println(foundetArticle)
                    }
                }
            }
            continue
        }

        // Artikel löschen
        if (selection == 5) {
            print("ID:")
            val id = readln().trim()
            if (!articles.any { it.id == id }) {
                println("Falscheingabe: Artikel mit ID $id nicht vorhanden.")
                continue
            }
            articles.removeIf { it.id == id }
            continue
        }
    }
}
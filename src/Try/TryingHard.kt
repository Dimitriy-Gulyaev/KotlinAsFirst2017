package Try

fun bigram(string: String): MutableMap<String, Int> {
    val bigr = mutableMapOf<String, Int>()
    val parts = Regex("""([^a-zA-Z])+""").split(string).filter{it != ""}
    for (element in parts)
        for (i in 1.until(element.length)) {
            val str = element[i-1].toString() + element[i]
            bigr[str]?.plus(1) ?: bigr.put(str, 1)
        }
    return bigr
}

fun dog(i: Int): Int = i + i
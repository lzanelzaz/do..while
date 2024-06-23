object StaticAnalyzer {

    fun analyze(text: String): String {
        return if (isError(text).isNotBlank()) {
            ""
        } else {
            text
        }
    }

    fun isError(text: String): String {
        return if (text.length % 2 == 0) {
            "Ошибка"
        } else {
            ""
        }
    }

    private fun parse() {

    }
}
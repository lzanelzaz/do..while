class Token(
    val type: TokenType? = null,
    val value: String? = null,
) {
    override fun toString() = "${type?.value ?: ""} ${value ?: ""}"
}

enum class TokenType(val value: String) {
    S("<Цикл do..while>"),
    d("<блок do>"),
    w("<блок while>"),
    A("<тело цикла>"),
    B("<условие>"),
    E("<выражение>"),
    V("<переменная>"),
    C("<операция сравнения>"),
    O("<арифметическая операция>"),
    D("<цифра>"),
}

class SyntaxTree(var root: Node? = null) {
    class Node(
        var value: Token,
        var left: Node? = null,
        var center: Node? = null,
        var right: Node? = null,
    )

    override fun toString(): String {
        return printTree(root, 0)
    }

    private fun printTree(root: Node?, level: Int): String {
        if (root == null) return ""
        var string = ""
        string += printTree(root.right, level + 1)

        string += "    ".repeat(level + 1) + root.value.toString() + "\n"

        string += printTree(root.center, level + 1)

        string += printTree(root.left, level + 1)
        return string
    }
}

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

    override fun toString(): String = printTree(root, isRoot = true)

    private fun printTree(
        root: Node?,
        padding: String = "",
        pointer: String = "",
        hasLeftSibling: Boolean = false,
        isRoot: Boolean = false,
    ): String {
        if (root == null) return ""
        var string = padding + pointer + root.value + "\n"

        val paddingForBoth = padding + if (isRoot) "" else {
            if (hasLeftSibling) "│     " else "      "
        }
        val pointerLeft = "└──"
        val pointerCenter = if (root.right != null) "├──" else "└──"
        val rightHasSibling = root.center != null || root.left != null
        val pointerRight = if (rightHasSibling) "├──" else "└──"
        string += printTree(root.right, paddingForBoth, pointerRight, rightHasSibling)
        string += printTree(root.center, paddingForBoth, pointerCenter, root.left != null)
        string += printTree(root.left, paddingForBoth, pointerLeft, false)
        return string
    }
}

import java.util.LinkedList
import java.util.Queue

object StaticAnalyzer {

    fun analyze(text: String): String {
        return try {
            parse(text).toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun parse(text: String): SyntaxTree {
        val deque: Queue<Char> = LinkedList(text.toList())
        return SyntaxTree(
            root = SyntaxTree.Node(
                value = Token(type = TokenType.S),
                left = deque.parse_d(),
                right = deque.parse_w(),
            )
        )
    }

    private fun Queue<Char>.parse_d(): SyntaxTree.Node {
        require(pollAlsoDropIfBlank() == 'd' && poll() == 'o') { "Блок do отсутствует" }
        require(pollAlsoDropIfBlank() == '{') { "Блок do не открыт - отсутствует '{'" }
        val centerNode = parse_A()
        require(pollAlsoDropIfBlank() == '}') { "Блок do не закрыт - отсутствует '}'" }
        return SyntaxTree.Node(
            value = Token(type = TokenType.d),
            left = SyntaxTree.Node(value = Token(value = "do {")),
            center = centerNode,
            right = SyntaxTree.Node(value = Token(value = "}")),
        )
    }

    private fun Queue<Char>.parse_w(): SyntaxTree.Node {
        require(
            pollAlsoDropIfBlank() == 'w' && poll() == 'h' &&
                    poll() == 'i' && poll() == 'l' &&
                    poll() == 'e'
        ) { "Блок while отсутствует" }
        require(pollAlsoDropIfBlank() == '(') { "Блок while не открыт - отсутствует '('" }
        val centerNode = parse_B()
        require(pollAlsoDropIfBlank() == ')') { "Блок while не закрыт - отсутствует ')'" }
        return SyntaxTree.Node(
            value = Token(type = TokenType.w),
            left = SyntaxTree.Node(value = Token(value = "while (")),
            center = centerNode,
            right = SyntaxTree.Node(value = Token(value = ")")),
        )
    }

    private fun Queue<Char>.parse_B(): SyntaxTree.Node {
        val variable = peekAlsoDropIfBlank()
        requireNotNull(variable) { "Условие в блоке while не дописано" }
        val expression1 = parse_E()
        requireNotNull(expression1) { "Условие в блоке while отсутствует" }
        val comparator = parse_comparator()
        val expression2 = parse_E()
        requireNotNull(expression2) { "Выражение в блоке while не закончено" }
        return SyntaxTree.Node(
            value = Token(
                type = TokenType.B,
                value = expression1.value.value + comparator.value.value + expression2.value.value,
            ),
            left = expression1,
            center = comparator,
            right = expression2,
        )
    }

    private fun Queue<Char>.parse_comparator(): SyntaxTree.Node {
        val first =
            pollAlsoDropIfBlank() ?: error("Ожидалась операция сравнения в блоке while")
        if (first !in comparators) error("Ожидалась операция сравнения в блоке while")
        val second = peekAlsoDropIfBlank() ?: error("Условие в блоке while не дописано")
        if (first !in listOf('=', '!') && second.isLetterOrDigit()) {
            return SyntaxTree.Node(
                value = Token(
                    type = TokenType.C,
                    value = first.toString(),
                )
            )
        } else {
            remove()
            if (second != '=') {
                error("Неизвестный токен \"$first\" в блоке while")
            } else {
                return SyntaxTree.Node(
                    value = Token(
                        type = TokenType.C,
                        value = first.toString() + second,
                    )
                )
            }
        }
    }

    private fun Queue<Char>.parse_A(): SyntaxTree.Node? {
        val variable = peekAlsoDropIfBlank()
        requireNotNull(variable) { "Тело цикла в блоке do не дописано" }
        if (variable == '}') return null
        remove()
        require(variable.isLetter()) { "В блоке do ожидалась переменная, а не \"$variable\"" }
        val op = pollAlsoDropIfBlank()
        require(op == '=') { "Ожидалась операция присваивания в блоке do, а не \"$op\"" }
        val expression = parse_E()
        requireNotNull(expression) { "Выражение в блоке do не закончено  \"${"$variable="}\"" }
        return SyntaxTree.Node(
            value = Token(
                type = TokenType.A,
                value = variable.toString() + '=' + expression.value.value,
            ),
            left = SyntaxTree.Node(value = Token(type = TokenType.V, value = variable.toString())),
            center = SyntaxTree.Node(value = Token(value = "=")),
            right = expression,
        )
    }

    private fun Queue<Char>.parse_E(): SyntaxTree.Node? {
        val expectedVarOrDigit = peekAlsoDropIfBlank() ?: return null
        if (expectedVarOrDigit == '}' || expectedVarOrDigit == ')') return null
        remove()
        require(expectedVarOrDigit.isLetterOrDigit()) { "Некорректное выражение \"$expectedVarOrDigit\"" }
        val expectedOperation = peekAlsoDropIfBlank()

        if (expectedOperation == null || expectedOperation == '}' || expectedOperation == ')'
            || isPossibleComparator(expectedOperation)
        ) {
            return SyntaxTree.Node(
                value = Token(
                    type = if (expectedVarOrDigit.isDigit()) TokenType.D else TokenType.V,
                    value = expectedVarOrDigit.toString(),
                ),
            )
        }
        remove()
        require(expectedOperation in operations) { "Символ \"$expectedOperation\" не поддерживается" }
        val right = parse_E()
        requireNotNull(right) { "Выражение \"${expectedVarOrDigit.toString() + expectedOperation}\" не закончено" }
        return SyntaxTree.Node(
            value = Token(
                type = TokenType.E,
                value = expectedVarOrDigit.toString() + expectedOperation + right.value.value,
            ),
            left = SyntaxTree.Node(
                value = Token(
                    type = if (expectedVarOrDigit.isDigit()) TokenType.D else TokenType.V,
                    value = expectedVarOrDigit.toString(),
                )
            ),
            center = SyntaxTree.Node(
                value = Token(
                    type = TokenType.O,
                    value = expectedOperation.toString()
                )
            ),
            right = right,
        )
    }

    private fun Queue<Char>.peekAlsoDropIfBlank(): Char? {
        var ch = peek()
        while (ch?.isWhitespace() == true) {
            remove()
            ch = peek()
        }
        return ch
    }

    private fun Queue<Char>.pollAlsoDropIfBlank(): Char? {
        var ch = poll()
        while (ch?.isWhitespace() == true) {
            ch = poll()
        }
        return ch
    }

    private fun isPossibleComparator(firstChar: Char): Boolean {
        return firstChar in comparators
    }

    private val operations = listOf('+', '-', '*', '/')
    private val comparators = listOf('>', '<', '=', '!')
}
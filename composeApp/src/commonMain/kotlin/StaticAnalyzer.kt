object StaticAnalyzer {

    fun analyze(text: String): String {
        return try {
            parse(text).toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    private fun parse(text: String): SyntaxTree {
        val deque: ArrayDeque<Char> = ArrayDeque(text.toList())
        return SyntaxTree(
            root = SyntaxTree.Node(
                value = Token(type = TokenType.S),
                left = deque.parse_d(),
                right = deque.parse_w(),
            )
        )
    }

    private fun ArrayDeque<Char>.parse_d(): SyntaxTree.Node {
        require(removeFirstAlsoDropIfBlank() == 'd' && removeFirstOrNull() == 'o') { "Блок do отсутствует" }
        require(removeFirstAlsoDropIfBlank() == '{') { "Блок do не открыт - отсутствует '{'" }
        val centerNode = parse_A()
        require(removeFirstAlsoDropIfBlank() == '}') { "Блок do не закрыт - отсутствует '}'" }
        return SyntaxTree.Node(
            value = Token(type = TokenType.d),
            left = SyntaxTree.Node(value = Token(value = "do {")),
            center = centerNode,
            right = SyntaxTree.Node(value = Token(value = "}")),
        )
    }

    private fun ArrayDeque<Char>.parse_w(): SyntaxTree.Node {
        require(
            removeFirstAlsoDropIfBlank() == 'w' && removeFirstOrNull() == 'h' &&
                    removeFirstOrNull() == 'i' && removeFirstOrNull() == 'l' &&
                    removeFirstOrNull() == 'e'
        ) { "Блок while отсутствует" }
        require(removeFirstAlsoDropIfBlank() == '(') { "Блок while не открыт - отсутствует '('" }
        val centerNode = parse_B()
        require(removeFirstAlsoDropIfBlank() == ')') { "Блок while не закрыт - отсутствует ')'" }
        return SyntaxTree.Node(
            value = Token(type = TokenType.w),
            left = SyntaxTree.Node(value = Token(value = "while (")),
            center = centerNode,
            right = SyntaxTree.Node(value = Token(value = ")")),
        )
    }

    private fun ArrayDeque<Char>.parse_B(): SyntaxTree.Node {
        val variable = firstAlsoDropIfBlank()
        requireNotNull(variable) { "Условие в блоке while не дописано" }
        val expression1 = parse_E()
        requireNotNull(expression1) { "Условие в блоке while отсутствует" }
        val comparator = parse_comporator()
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

    private fun ArrayDeque<Char>.parse_comporator(): SyntaxTree.Node {
        val first =
            removeFirstAlsoDropIfBlank() ?: error("Ожидалась операция сравнения в блоке while")
        if (first !in comparators) error("Ожидалась операция сравнения в блоке while")
        val second = firstAlsoDropIfBlank() ?: error("Условие в блоке while не дописано")
        if (first !in listOf('=', '!') && second.isLetterOrDigit()) {
            return SyntaxTree.Node(
                value = Token(
                    type = TokenType.C,
                    value = first.toString(),
                )
            )
        } else {
            removeFirst()
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

    private fun ArrayDeque<Char>.parse_A(): SyntaxTree.Node? {
        val variable = firstAlsoDropIfBlank()
        requireNotNull(variable) { "Тело цикла в блоке do не дописано" }
        if (variable == '}') return null
        removeFirst()
        require(variable.isLetter()) { "В блоке do ожидалась переменная, а не \"$variable\"" }
        val op = removeFirstAlsoDropIfBlank()
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

    private fun ArrayDeque<Char>.parse_E(): SyntaxTree.Node? {
        val expectedVarOrDigit = firstAlsoDropIfBlank() ?: return null
        if (expectedVarOrDigit == '}' || expectedVarOrDigit == ')') return null
        removeFirst()
        require(expectedVarOrDigit.isLetterOrDigit()) { "Некорректное выражение \"$expectedVarOrDigit\"" }
        val expectedOperation = firstAlsoDropIfBlank()

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
        removeFirst()
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

    private fun ArrayDeque<Char>.firstAlsoDropIfBlank(): Char? {
        var ch = firstOrNull()
        while (ch?.isWhitespace() == true) {
            removeFirst()
            ch = firstOrNull()
        }
        return ch
    }

    private fun ArrayDeque<Char>.removeFirstAlsoDropIfBlank(): Char? {
        var ch = removeFirstOrNull()
        while (ch?.isWhitespace() == true) {
            ch = removeFirstOrNull()
        }
        return ch
    }

    private fun isPossibleComparator(firstChar: Char): Boolean {
        return firstChar in comparators
    }

    private val operations = listOf('+', '-', '*', '/')
    private val comparators = listOf('>', '<', '=', '!')
}
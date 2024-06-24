import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var text by rememberSaveable { mutableStateOf("") }
        var outputText by rememberSaveable { mutableStateOf("") }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = Dp(36f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth().padding(vertical = Dp(36f))) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = "Запуск анализа",
                        fontSize = TextUnit(24f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                    )
                }
                IconButton(onClick = {
                    outputText = StaticAnalyzer.analyze(text)
                }) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(Dp(80f)),
                        tint = Color(0xFF298b28),
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = Dp(36f))) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f),
                    label = {
                        Text(
                            text = "Введите текст для анализа",
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                        )
                    },
                )
                Spacer(Modifier.width(Dp(36f)))
                OutlinedTextField(
                    value = outputText,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.fillMaxHeight().fillMaxWidth().weight(1f),
                    label = {
                        Text(
                            text = "Построенное синтаксическое дерево",
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                        )
                    },
                )
            }

        }
    }
}

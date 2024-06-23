import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
        var errorText by rememberSaveable { mutableStateOf("") }
        var outputText by rememberSaveable { mutableStateOf("") }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = Dp(36f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth().padding(top = Dp(36f))) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = "Запуск анализа",
                        fontSize = TextUnit(24f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                    )
                }
                IconButton(onClick = {
                    outputText = StaticAnalyzer.analyze(text)
                    errorText = StaticAnalyzer.isError(text)
                }) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(Dp(80f)),
                        tint = Color(0xFF298b28),
                    )
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    errorText = ""
                },
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f).padding(vertical = Dp(36f)),
                isError = errorText.isNotBlank(),
                supportingText = {
                    Text(
                        text = errorText,
                        fontSize = TextUnit(16f, TextUnitType.Sp),
                    )
                },
            )
            OutlinedTextField(
                value = outputText,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = Dp(36f)),
            )
        }
    }
}

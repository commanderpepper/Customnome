package commanderpepper.customnome.feature.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp), text = "Customnome", fontSize = 20.sp)
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = "Customnome is an app where you can choose the BPM and the sound for a metronome.",
            fontSize = 14.sp
        )
        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = buildAnnotatedString {
            withLink(
                LinkAnnotation.Url(
                    "https://github.com/commanderpepper/Customnome",
                    TextLinkStyles(style = SpanStyle(color = Color.Blue))
                )
            )
            {
                append("App source code")
            }
        }, fontSize = 14.sp)
        Column {
            Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = "Libraries Used", fontSize = 14.sp)
            Text(modifier = Modifier.padding(vertical = 2.dp, horizontal = 20.dp), text = "Kotlin", fontSize = 12.sp)
            Text(modifier = Modifier.padding(vertical = 2.dp, horizontal = 20.dp), text = "Compose", fontSize = 12.sp)
            Text(modifier = Modifier.padding(vertical = 2.dp, horizontal = 20.dp), text = "Exoplayer", fontSize = 12.sp)
            Text(modifier = Modifier.padding(vertical = 2.dp, horizontal = 20.dp), text = "Koin", fontSize = 12.sp)
            Text(modifier = Modifier.padding(vertical = 2.dp, horizontal = 20.dp), text = "Inkscape", fontSize = 12.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InfoScreenPreview() {
    InfoScreen()
}
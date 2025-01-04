package commanderpepper.customnome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import commanderpepper.customnome.ui.theme.CustomnomeTheme
import commanderpepper.featuremetronome.MetronomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomnomeTheme {
                MetronomeScreen()
            }
        }
    }
}

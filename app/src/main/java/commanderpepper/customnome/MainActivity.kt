package commanderpepper.customnome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import commanderpepper.customnome.feature.info.InfoScreen
import commanderpepper.featuremetronome.MetronomeScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Metronome) {
                composable<Metronome> {
                    Scaffold(topBar = {
                        TopAppBar(title = { Text("Customnome") }, actions = {
                            Image(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .clickable { navController.navigate(route = Information) },
                                painter = painterResource(R.drawable.baseline_info_outline_24),
                                contentDescription = "Info"
                            )
                        })
                    }) { scaffoldPadding ->
                        MetronomeScreen(modifier = Modifier.padding(scaffoldPadding))
                    }
                }
                composable<Information> {
                    InfoScreen()
                }
            }
        }
    }
}

@Serializable
object Metronome

@Serializable
object Information

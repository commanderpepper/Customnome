package commanderpepper.featuremetronome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@Composable
fun MetronomeScreen(modifier: Modifier = Modifier.fillMaxSize(), viewModel: MetronomeViewModel = viewModel()){
    val uiState = viewModel.uiState.collectAsState()
    MetronomeScreen(modifier = modifier, uiState = uiState.value.metronomeUIState) { slider ->
        viewModel.updateBPM(slider)
    }
}

@Composable
fun MetronomeScreen(modifier: Modifier = Modifier.fillMaxSize(), uiState: MetronomeUIState, onValueChange: (Float) -> Unit) {
    Column(modifier = modifier) {
        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = uiState.beatsPerMinute, fontSize = 24.sp)
        Slider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
            value = uiState.value,
            steps = 178,
            valueRange = 40f .. 218f,
            onValueChange = {
                onValueChange(it.roundToInt().toFloat())
            }
        )
    }
}

@Preview
@Composable
fun MetronomeScreenPreview(){
    MetronomeScreen(uiState = MetronomeUIState("100 bpm", 100f)) {  }
}

data class MetronomeUIState(val beatsPerMinute: String, val value: Float)
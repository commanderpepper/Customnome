package commanderpepper.featuremetronome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MetronomeViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MetronomeScreenUIState(MetronomeUIState("bmp: 89", 89f)))
    val uiState = _uiState.asStateFlow()

    fun updateBPM(bpm: Float){
        _uiState.value = MetronomeScreenUIState(MetronomeUIState(beatsPerMinute = "bpm: ${bpm.toInt()}", value = bpm))
    }
}

data class MetronomeScreenUIState(val metronomeUIState: MetronomeUIState)
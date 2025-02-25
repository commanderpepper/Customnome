package commanderpepper.featuremetronome

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.time.delay
import java.time.Duration
import kotlin.math.roundToInt

@Composable
fun MetronomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: MetronomeViewModel = viewModel(),
    audioFileId: Int
){

    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
    val playerState = viewModel.playerState.collectAsState()
    val bmp = viewModel.bpm.collectAsState()
    val isPlaying = viewModel.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(audioFileId.toString()).build()
        if(uri != null){
            viewModel.initializePlayer(context, uri)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.savePlayerState()
            viewModel.releasePlayer()
        }
    }

    LaunchedEffect(isPlaying.value) {
        while (isPlaying.value){
            playerState.value?.seekTo(0)
            playerState.value?.play()
            delay(Duration.ofMillis(bmp.value.toLong()))
        }
    }

    MetronomeScreen(
        modifier = modifier,
        uiState = uiState.value.metronomeUIState,
        player = playerState.value,
        onPlay = viewModel::playPlayer,
        onPause = viewModel::pausePlayer,
        onValueChange = { slider ->
            viewModel.updateBPM(slider)
        })
}

@Composable
fun MetronomeScreen(modifier: Modifier = Modifier.fillMaxSize(), uiState: MetronomeUIState, player: ExoPlayer?, onValueChange: (Float) -> Unit, onPlay: () -> Unit, onPause: () -> Unit) {
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
        PlayerControls(
            player,
            onPlay = onPlay,
            onPause = onPause
        )
    }
}

@Composable
fun PlayerControls(player: ExoPlayer?, onPlay: () -> Unit, onPause: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPlay) {
            Text("Play")
        }
        Button(onClick = onPause) {
            Text("Pause")
        }
    }
}

@Preview
@Composable
fun MetronomeScreenPreview(){
    MetronomeScreen(uiState = MetronomeUIState("100 bpm", 100f), player = null,
        onValueChange = {}, onPlay = {}, onPause = {})
}

data class MetronomeUIState(val beatsPerMinute: String, val value: Float)
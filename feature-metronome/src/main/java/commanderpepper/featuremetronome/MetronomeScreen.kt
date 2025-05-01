package commanderpepper.featuremetronome

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.time.delay
import org.koin.compose.viewmodel.koinViewModel
import java.time.Duration
import kotlin.math.roundToInt

@Composable
fun MetronomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MetronomeViewModel = koinViewModel<MetronomeViewModel>()
) {

    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
    val fileName = viewModel.fileName.collectAsState()
    val playerState = viewModel.playerState.collectAsState()
    val bmp = viewModel.bpm.collectAsState()
    val isPlaying = viewModel.isPlaying.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_RESUME){
                viewModel.playPlayer()
            }
            if(event == Lifecycle.Event.ON_PAUSE){
                viewModel.pausePlayer()
            }
            if(event == Lifecycle.Event.ON_DESTROY){
                viewModel.releasePlayer()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isPlaying.value) {
        while (isPlaying.value) {
            playerState.value?.seekTo(0)
            playerState.value?.play()
            delay(Duration.ofMillis(bmp.value.toLong()))
        }
    }

    MetronomeScreen(
        modifier = modifier,
        uiState = uiState.value.metronomeUIState,
        isPlaying = isPlaying.value,
        onPlay = viewModel::playPlayer,
        onPause = viewModel::pausePlayer,
        onFileSelected = viewModel::setUri,
        onDefault = viewModel::setToDefault,
        onMinus = viewModel::decreaseBPM,
        onPlus = viewModel::increaseBPM,
        fileName = fileName.value,
        onValueChange = { slider ->
            viewModel.updateBPM(slider)
        })
}

@Composable
fun MetronomeScreen(
    modifier: Modifier = Modifier,
    fileName: String,
    uiState: MetronomeUIState,
    isPlaying: Boolean,
    onFileSelected: (Uri) -> Unit,
    onValueChange: (Float) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onDefault: () -> Unit,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val localContext = LocalContext.current
        val fileLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
                if (result != null) {
                    localContext.contentResolver.takePersistableUriPermission(
                        result,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    onFileSelected(result)
                }
            }

        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), text = fileName)
        Row {
            Button(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                onClick = { fileLauncher.launch(arrayOf("audio/*")) }) { Text("Choose a file") }
            Button(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                onClick = { onDefault()}) { Text("Use default sound") }
        }
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = uiState.beatsPerMinute,
            fontSize = 24.sp
        )
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            value = uiState.value,
            steps = 178,
            valueRange = 40f..218f,
            onValueChange = {
                onValueChange(it.roundToInt().toFloat())
            }
        )
        PlayerControls(
            isPlaying = isPlaying,
            onPlay = onPlay,
            onPause = onPause,
            onPlus = onPlus,
            onMinus = onMinus
        )
    }
}

@Composable
fun PlayerControls(isPlaying: Boolean, onMinus: () -> Unit, onPlus: () -> Unit, onPlay: () -> Unit, onPause: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ){
        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(64.dp)
                .clip(CircleShape),
            onClick = onMinus
        ) {
            Text("-1", color = Color.Black)
        }
        Button(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            onClick = if (isPlaying) onPause else onPlay
        ) {
            AnimatedVisibility(isPlaying) {
                Image(
                    contentScale = ContentScale.FillBounds,
                    painter = painterResource(R.drawable.baseline_pause_24),
                    contentDescription = "Pause"
                )
            }
            Image(
                contentScale = ContentScale.FillBounds,
                painter = painterResource(R.drawable.baseline_play_arrow_24),
                contentDescription = "Play"
            )
        }
        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(64.dp)
                .clip(CircleShape),
            onClick = onPlus
        ) {
            Text("+1", color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MetronomeScreenPreview() {
    MetronomeScreen(
        uiState = MetronomeUIState(beatsPerMinute = "100 bpm", value = 100f),
        fileName = "name",
        isPlaying = true,
        onValueChange = {},
        onPlay = {},
        onPause = {},
        onDefault = {},
        onPlus = {},
        onMinus = {},
        onFileSelected = {})
}

data class MetronomeUIState(val beatsPerMinute: String, val value: Float)
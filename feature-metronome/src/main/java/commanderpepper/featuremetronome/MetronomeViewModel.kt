package commanderpepper.featuremetronome

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import commanderpepper.customnome.data.local.URIRetriever
import commanderpepper.customnome.data.local.URIWithName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MetronomeViewModel(private val uriRetriever: URIRetriever): ViewModel() {

    private val _fileName = MutableStateFlow("")
    val fileName = _fileName.asStateFlow()

    private val _uiState = MutableStateFlow(MetronomeScreenUIState(metronomeUIState = MetronomeUIState(beatsPerMinute = "bmp: 89", value = 89f)))
    val uiState = _uiState.asStateFlow()

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    private val _isPlaying = MutableStateFlow<Boolean>(true)
    val isPlaying = _isPlaying

    val bpm = _uiState.map { (60f / it.metronomeUIState.value) * 1000 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 89 * 1000f)

    private var currentPosition: Long = 0L

    fun updateBPM(bpm: Float){
        _uiState.value = MetronomeScreenUIState(metronomeUIState = MetronomeUIState(beatsPerMinute = "bpm: ${bpm.toInt()}", value = bpm))
    }

    fun initializePlayer(context: Context) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    it.prepare()
                    it.playWhenReady = false
                    it.seekTo(currentPosition)
                    it.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            handleError(error)
                        }
                    })
                }

                _playerState.value = exoPlayer
                val lastSavedUri = uriRetriever.getURI()
                playUri(lastSavedUri)
            }
        }
    }

    fun setUri(uri: Uri){
        uriRetriever.storeURI(uri)
        val uriWithName = uriRetriever.getURI()
        playUri(uriWithName)
    }

    private fun playUri(uri: URIWithName){
        try {
            _fileName.value = uri.name
            _playerState.value?.setMediaItem(MediaItem.fromUri(uri.uri))
        }
        catch (e: Exception){
            val defaultUri = uriRetriever.getDefaultURI()
            _fileName.value = defaultUri.name
            _playerState.value?.setMediaItem(MediaItem.fromUri(defaultUri.uri))
        }
    }

    fun savePlayerState() {
        _playerState.value?.let {
            currentPosition = it.currentPosition
        }
    }

    fun releasePlayer() {
        _playerState.value?.release()
        _playerState.value = null
    }

    fun playPlayer(){
        _playerState.value?.play()
        _isPlaying.value = true
    }

    fun pausePlayer(){
        _playerState.value?.pause()
        _isPlaying.value = false
    }

    private fun handleError(error: PlaybackException) {
        when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                // Handle file not found error
                println("File not found")
            }

            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                // Handle decoder initialization error
                println("Decoder initialization error")
            }

            else -> {
                // Handle other types of errors
                println("Other error: ${error.message}")
            }
        }
    }
}

data class MetronomeScreenUIState(val metronomeUIState: MetronomeUIState)
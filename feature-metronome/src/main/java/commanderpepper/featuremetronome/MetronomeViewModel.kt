package commanderpepper.featuremetronome

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import commanderpepper.customnome.data.local.URIRetriever
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class MetronomeViewModel(application: Application, private val uriRetriever: URIRetriever): AndroidViewModel(application) {

    private val _fileName = MutableStateFlow("metronome_sound.wav")
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

    fun initializePlayer(context: Context, uri: Uri) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    val mediaItem = MediaItem.fromUri(uri)
                    it.setMediaItem(mediaItem)
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
            }
        }
    }

    fun setUri(uri: Uri){
        uriRetriever.storeURI(uri)
        val uriWithName = uriRetriever.getURI()
        _fileName.value = uriWithName.name
        _playerState.value?.setMediaItem(MediaItem.fromUri(uriWithName.uri))
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

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = getApplication<Application>().contentResolver.query(uri, null, null, null, null)
            cursor.use { openCursor ->
                if (openCursor != null && openCursor.moveToFirst()) {
                    if(openCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) >= 0){
                        result = openCursor.getString(openCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result ?: ""
    }

}

data class MetronomeScreenUIState(val metronomeUIState: MetronomeUIState)
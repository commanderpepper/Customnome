package commanderpepper.customnome.data.local

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import commanderpepper.customnome.data.R

class URIRetrieverImpl(private val application: Application) : URIRetriever {
    private val sharedPreferences = application.applicationContext?.getSharedPreferences("uris", Context.MODE_PRIVATE)

    override fun getURI(): URIWithName {
        return try {
            val soundUri = sharedPreferences?.getString("sound", null)?.toUri()
            if(soundUri != null){
                val fileName = getFileName(soundUri)
                URIWithName(soundUri, fileName)
            }
            else {
                getDefaultURI()
            }
        }
        catch (e: Exception){
            getDefaultURI()
        }
    }

    override fun storeURI(uri: Uri) {
        sharedPreferences?.edit()?.putString("sound", uri.toString())?.apply()
    }

    override fun getDefaultURI(): URIWithName {
        val uri = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(R.raw.metronome_sound.toString()).build()
        return URIWithName(uri, "metronome_sound.wav")
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = application.contentResolver.query(uri, null, null, null, null)
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
package commanderpepper.customnome.data.local

import android.net.Uri

interface URIRetriever {
    fun getURI(): URIWithName

    fun storeURI(uri: Uri)
}

data class URIWithName(val uri: Uri, val name: String)
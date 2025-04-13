package commanderpepper.customnome

import android.app.Application
import commanderpepper.featuremetronome.di.metronomeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MetronomeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MetronomeApplication)
            modules(listOf(metronomeModule))
        }
    }
}
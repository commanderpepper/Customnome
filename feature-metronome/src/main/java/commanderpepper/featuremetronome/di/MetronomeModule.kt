package commanderpepper.featuremetronome.di

import commanderpepper.featuremetronome.MetronomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val metronomeModule = module {
    viewModel { MetronomeViewModel(get()) }
}
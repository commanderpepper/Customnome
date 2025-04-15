package commanderpepper.customnome.data.local.di

import commanderpepper.customnome.data.local.URIRetriever
import commanderpepper.customnome.data.local.URIRetrieverImpl
import org.koin.dsl.module

val localModule = module {
    single<URIRetriever> { URIRetrieverImpl(get()) }
}
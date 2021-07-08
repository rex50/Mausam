package com.rex50.mausam.di

import android.content.Context
import com.rex50.mausam.views.fragments.discover.FragDiscoverViewModel
import com.rex50.mausam.views.fragments.favourites.FragFavouritesViewModel
import com.rex50.mausam.views.fragments.home.FragHomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

object UIDependencySetup {

    private val uiModule = module {
        single { FragFavouritesViewModel(get(), get()) }
        single { FragHomeViewModel() }
        single { FragDiscoverViewModel(get(), get(), get()) }
    }

    @JvmStatic
    fun inject(context: Context){
        startKoin {
            androidContext(context)
        }
        loadKoinModules(
            uiModule
        )
        RepositoryDependencySetup.inject()
    }
}
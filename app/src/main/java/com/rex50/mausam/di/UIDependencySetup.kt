package com.rex50.mausam.di

import android.content.Context
import com.rex50.mausam.views.activities.auto_wallpaper.ActAutoWallpaperViewModel
import com.rex50.mausam.views.activities.collections.ActCollectionsListViewModel
import com.rex50.mausam.views.activities.neon_editor.ActNeonEditorViewModel
import com.rex50.mausam.views.activities.photos.ActPhotosListViewModel
import com.rex50.mausam.views.activities.settings.ActSettingsViewModel
import com.rex50.mausam.views.fragments.discover.FragDiscoverViewModel
import com.rex50.mausam.views.fragments.favourites.FragGalleryViewModel
import com.rex50.mausam.views.fragments.home.FragHomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

object UIDependencySetup {

    private val uiModule = module {
        viewModel { FragGalleryViewModel(get(), get()) }
        viewModel { FragHomeViewModel(get(), get()) }
        viewModel { FragDiscoverViewModel(get(), get()) }
        viewModel { ActPhotosListViewModel(get(), get()) }
        viewModel { ActCollectionsListViewModel(get(), get()) }
        viewModel { ActSettingsViewModel(get()) }
        viewModel { ActAutoWallpaperViewModel(get()) }
        viewModel { ActNeonEditorViewModel(get(), get()) }
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

        UtilsDependencySetup.inject()
    }
}
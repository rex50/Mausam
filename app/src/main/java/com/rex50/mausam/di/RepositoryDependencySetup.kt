package com.rex50.mausam.di

import com.rex50.mausam.network.APIManager
import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.MausamSharedPrefs
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object RepositoryDependencySetup {

    private val repositoryModule = module {
        single { KeyValuesRepository(get()) }
        single { UnsplashHelper(get(), get(), get()) }
        single { MausamSharedPrefs(get()) }
    }

    private val networkModule = module {
        single { APIManager.getInstance(get()) }
    }

    @JvmStatic
    fun inject() {
        loadKoinModules(listOf(
            repositoryModule,
            networkModule
        ))
    }

}
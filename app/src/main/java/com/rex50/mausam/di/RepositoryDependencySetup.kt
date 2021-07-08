package com.rex50.mausam.di

import com.rex50.mausam.network.UnsplashHelper
import com.rex50.mausam.storage.database.key_values.KeyValuesRepository
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object RepositoryDependencySetup {

    private val repositoryModule = module {
        single { KeyValuesRepository(get()) }
        single { UnsplashHelper(get()) }
    }

    @JvmStatic
    fun inject() {
        loadKoinModules(
            repositoryModule
        )

        UtilsDependencySetup.inject()
    }

}
package com.rex50.mausam.di

import com.rex50.mausam.utils.ConnectionChecker
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object UtilsDependencySetup {

    private val utilsModule = module {
        single { ConnectionChecker(get()) }
    }

    @JvmStatic
    fun inject() {
        loadKoinModules(
            utilsModule
        )
    }

}
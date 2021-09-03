package com.rex50.mausam.di

import com.rex50.mausam.utils.ConnectionChecker
import com.rex50.mausam.utils.GradientHelper
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

object UtilsDependencySetup {

    private val utilsModule = module {
        single { ConnectionChecker(get()) }
        single { GradientHelper.getInstance(get()) }
    }

    @JvmStatic
    fun inject() {
        loadKoinModules(
            utilsModule
        )
    }

}
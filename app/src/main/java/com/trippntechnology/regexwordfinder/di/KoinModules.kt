package com.trippntechnology.regexwordfinder.di

import com.trippntechnology.regexwordfinder.ux.mainactivity.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun getAllKoinModules(): List<Module> = listOf(viewModelModule)

val viewModelModule = module {
    viewModel { MainActivityViewModel(androidApplication()) }
}

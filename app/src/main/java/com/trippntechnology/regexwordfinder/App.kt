package com.trippntechnology.regexwordfinder

import android.app.Application
import com.trippntechnology.regexwordfinder.di.getAllKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.logger.Level
import org.koin.dsl.koinConfiguration

// WARNING: DON'T add startup logic here...
//   If you need to add any startup logic, use an Initializer (see startup package)
//   Don't forget to register the initializer in the AndroidManifest.xml
class App : Application(), KoinStartup {
    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@App)
        androidLogger(Level.INFO)
        modules(getAllKoinModules())
    }
}

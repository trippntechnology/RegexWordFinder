package com.trippntechnology.regexwordfinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// WARNING: DON'T add startup logic here...
//   If you need to add any startup logic, use an Initializer (see startup package)
//   Don't forget to register the initializer in the AndroidManifest.xml
@HiltAndroidApp
class App : Application()
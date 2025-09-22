object AppInfo {
    const val APPLICATION_ID = "com.trippntechnology.regexwordfinder"

    // Manifest version information
    object Version {
        private const val SEMANTIC_NAME = "1.0.1"
        val CODE = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        val BUILD_NUMBER = System.getenv("BUILD_NUMBER") ?: "0"
        val NAME = "$SEMANTIC_NAME-($CODE.$BUILD_NUMBER)"
    }

    object AndroidSdk {
        const val MIN = 26
        const val COMPILE = 36
        const val TARGET = COMPILE
    }
}

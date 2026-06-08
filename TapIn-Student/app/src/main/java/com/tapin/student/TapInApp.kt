package com.tapin.student

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point.
 * @HiltAndroidApp triggers Hilt's code generation and
 * installs the base application-level component.
 */
@HiltAndroidApp
class TapInApp : Application()

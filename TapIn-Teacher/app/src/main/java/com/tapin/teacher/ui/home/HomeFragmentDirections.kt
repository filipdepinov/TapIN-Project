package com.tapin.teacher.ui.home

import androidx.navigation.NavDirections
import androidx.core.os.bundleOf

/**
 * Navigation directions for HomeFragment.
 * Safe Args would auto-generate this; we write it manually so the project
 * compiles without the Gradle plugin needing to run first.
 */
object HomeFragmentDirections {

    fun actionHomeToSession(
        courseId: String,
        courseName: String,
        courseCode: String
    ): NavDirections = object : NavDirections {
        override val actionId = com.tapin.teacher.R.id.action_home_to_session
        override val arguments = bundleOf(
            "courseId"   to courseId,
            "courseName" to courseName,
            "courseCode" to courseCode
        )
    }
}

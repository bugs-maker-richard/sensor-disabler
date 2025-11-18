package com.wardellbagby.sensordisabler.util

import android.content.SharedPreferences

/**
 * Extension functions for managing file-based sensor data preferences.
 */

/**
 * Gets whether file-based sensor data reading is enabled.
 */
fun SharedPreferences.isFileDataEnabled(): Boolean {
  return getBoolean(Constants.PREFS_KEY_USE_FILE_DATA, false)
}

/**
 * Sets whether file-based sensor data reading is enabled.
 */
fun SharedPreferences.setFileDataEnabled(enabled: Boolean) {
  edit().putBoolean(Constants.PREFS_KEY_USE_FILE_DATA, enabled).apply()
}

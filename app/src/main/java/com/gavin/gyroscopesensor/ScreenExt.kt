package com.gavin.gyroscopesensor

import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity

/**
 * Author:     gavinsong
 * Date:       2020/3/25
 * Description:
 *----------------------------------------------------------------------------------
 */

fun getDisplayDensity(activity: AppCompatActivity): Int {
    var metric = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(metric)
    return metric.densityDpi
}
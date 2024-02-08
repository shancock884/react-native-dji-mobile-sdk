package com.reactnativedjimobilesdk

import android.util.Base64
import android.util.Log
import com.facebook.react.bridge.*
import dji.common.error.DJIError
import dji.sdk.mission.waypoint.*
import dji.sdk.sdkmanager.DJISDKManager
import java.util.*
import kotlin.concurrent.schedule

class DJISDKMissionWrapper(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  override fun getName(): String {
    return "DJISDKMissionWrapper"
  }

  private fun retrieveWPOperator(): WaypointMissionOperator {
    val sdkManager = DJISDKManager.getInstance()
    return sdkManager.getMissionControl().getWaypointMissionOperator()
  }

  @ReactMethod
  fun getCurrentState(promise: Promise) {
    try {
      val wpOperator = retrieveWPOperator()
      val state = wpOperator.currentState
      promise.resolve(state)
    } catch (e: Exception) {
      promise.reject(e.toString(), e.message)
    }
  }
}


package com.reactnativedjimobilesdk
import android.util.Log
import com.facebook.react.bridge.*
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager

const val TAG = "REACT-DJI"

class DJISDKManagerWrapper(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  val reactEventEmitter = ReactEventEmitter(reactContext)

  override fun getName(): String {
    return "DJISDKManagerWrapper"
  }

  @ReactMethod
  fun getSDKVersion(promise: Promise) {
    Log.i(TAG, "Get SDK version")
    val sdkManager = DJISDKManager.getInstance()
    val sdkVer = sdkManager.getSDKVersion()
    promise.resolve(sdkVer)
  }

  @ReactMethod
  fun registerApp(promise: Promise) {
    // NEED TO BE CALL FIRST BEFORE ALLLLLLLL
    // TODO Call it just after constructor
    com.secneo.sdk.Helper.install(currentActivity?.application)
    // TODO Check if already registering an APP
    Log.d(TAG, "Register APP")
    val sdkManager = DJISDKManager.getInstance()
    sdkManager.registerApp(reactApplicationContext.applicationContext,
      object : DJISDKManager.SDKManagerCallback {
        override fun onRegister(djiError: DJIError?) {
          if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
            Log.d(TAG, "Registration Success")
            reactEventEmitter.sendEvent(ReactEventEmitter.Event.REGISTRATION_SUCCESS, null)
            promise.resolve(true)
          } else {
            Log.e(TAG, "Fail Register")
            promise.reject(djiError.toString(), djiError?.description);
          }
        }

        override fun onProductDisconnect() {
          Log.i(TAG, "Product disconnected")
          reactEventEmitter.sendEvent(ReactEventEmitter.Event.PRODUCT_DISCONNECTED, null)
        }

        override fun onProductConnect(baseProduct: BaseProduct?) {
          Log.i(TAG, "Product connected")
          reactEventEmitter.sendEvent(ReactEventEmitter.Event.PRODUCT_CONNECTED, null)
          promise.resolve("[REACT-DJI] Product Connected successfully");
        }

        override fun onProductChanged(p0: BaseProduct?) {
          Log.i(TAG, "Product changed")
        }

        override fun onComponentChange(
          componentKey: BaseProduct.ComponentKey?,
          oldComponent: BaseComponent?,
          newComponent: BaseComponent?
        ) {
          Log.i(TAG, "onComponentChange Not yet implemented")
        }

        override fun onInitProcess(djiSdkInitEvent: DJISDKInitEvent?, process: Int) {
          Log.i(TAG, "onInitProcess Not yet implemented")
        }

        override fun onDatabaseDownloadProgress(p0: Long, p1: Long) {
          Log.i(TAG, "onDatabaseDownload Not yet implemented")
        }
      })
  }

  @ReactMethod
  fun startConnectionToProduct(promise: Promise) {
    Log.i(TAG, "Connect to product")
    val sdkManager = DJISDKManager.getInstance()
    sdkManager.startConnectionToProduct();
    promise.resolve(null)
  }
}

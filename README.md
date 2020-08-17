# Oxagile IT app for Android

The flipper tool was added in the app for debugging. More info: https://fbflipper.com/

# Silent Android App Update

The app can be updated silently without user confirmation with a device owner app. 
It works on Android 6.0 and higher. 

TODO: Test on Android 5.

For more information see 
https://sisik.eu/blog/android/dev-admin/update-app

# Usage
1. Build app and install on device.
```
adb install -r -t app.apk
```
Where "app.apk" is name of APK-file of the app.<br>

2. Set app as device owner (Important condition: on device must have no any account).
```
adb shell dpm set-device-owner com.oxagile.itapp/.receiver.DevAdminReceiver
```
>For Android 7.0 and higher you also need to set the `testOnly` flag in AndroidManifest.xml (note for Android Developer)
<br>

3. Increment versionCode of the app and rebuild again.
>On Android 7.0 and higher you might need to **remove** the `testOnly` flag from AndroidManifest.xml again for the updates 
>(note for Android Developer)
<br>

4. Push the new APK with updated versionCode to the device (this step for testing of app updating).
```
adb push app.apk /storage/emulated/0/Android/data/com.oxagile.itapp/files/
```
<br>
Where "app.apk" is name of APK-file of the app with new version of code.
<br>



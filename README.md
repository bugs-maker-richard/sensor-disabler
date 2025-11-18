Sensor Disabler
========================

This Xposed module allows you to modify and disable various sensors on your device. It supports Tasker and filtering which apps will be able to bypass Sensor Disabler.

It has been testing and confirmed working from SDK 21 (Lollipop) to SDK 29 (Pie).

Features
--------
 - Disable or mock any sensor on your device
 - Configure sensor values through the app UI or custom text files
 - Filter which apps are affected by sensor modifications
 - Tasker integration for automation
 - Import/export sensor configurations via text files

Installation
------------
 1. Download and install [Xposed framework](http://repo.xposed.info/module/de.robv.android.xposed.installer)
 2. Search for and install [Sensor Disabler](https://play.google.com/store/apps/details?id=com.mrchandler.disableprox) module. Alternatively: Download the APK from one of the Release tags or build it from source.
 3. Activate the module and reboot

Custom Sensor Data Files
-------------------------
Sensor Disabler can read sensor mock values from custom text files, allowing for easier bulk configuration and data import.

See [SENSOR_DATA_FILE.md](SENSOR_DATA_FILE.md) for detailed documentation on this feature.

Quick start:
 1. Create a file at `/sdcard/SensorDisabler/sensor_data.txt`
 2. Add sensor configurations in the format: `SensorKey=value1:value2:value3`
 3. Enable "Use File Data" in app settings
 4. Reboot or restart target apps

Links
-----
 - [Xposed Module page](http://repo.xposed.info/module/com.mrchandler.disableprox)
 - [Support](http://forum.xda-developers.com/xposed/modules/mod-disable-proximity-t2798887)
 - [Google Play](https://play.google.com/store/apps/details?id=com.mrchandler.disableprox)

# Custom Sensor Data File Feature

## Overview

This feature allows Sensor Disabler to read sensor mock values from a custom text file stored on the device's external storage. This provides an alternative to manually configuring each sensor through the app's UI and enables easier bulk configuration and data import.

## How It Works

When enabled, Sensor Disabler will attempt to read sensor values from a text file before falling back to the SharedPreferences-based configuration. This allows you to:

- Configure multiple sensors at once by editing a single text file
- Share sensor configurations between devices
- Backup and restore sensor settings
- Programmatically generate sensor configurations

## File Location

The sensor data file should be placed at:
```
/sdcard/SensorDisabler/sensor_data.txt
```

The directory will be automatically created when the feature is first used.

## File Format

The text file uses a simple key-value format:

```
# Comments start with #
SensorKey=value1:value2:value3
```

### Format Details

- **Comments**: Lines starting with `#` are treated as comments and ignored
- **Empty Lines**: Empty lines are ignored
- **SensorKey**: The unique identifier for a sensor (format described below)
- **Values**: Colon-separated float values representing the mock sensor data
- **Separator**: Use `=` between the key and values
- **Value Separator**: Use `:` between multiple values

### Sensor Key Format

The sensor key follows this pattern:
```
SensorName|*^&SensorDisabler&^*|Vendor|*^&SensorDisabler&^*|Version|*^&SensorDisabler&^*|Type
```

For example:
```
Accelerometer Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|1
```

### Finding Sensor Keys

You can find the exact sensor keys for your device by:

1. Opening the Sensor Disabler app
2. Viewing the sensor list
3. The sensor information includes name, vendor, version, and type
4. Combine these using the separator pattern shown above

## Example Configuration

Here's an example `sensor_data.txt` file:

```
# Accelerometer - stationary device on flat surface
# X=0, Y=0, Z=9.8 (gravity)
Accelerometer Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|1=0.0:0.0:9.8

# Proximity - far from object (5 cm)
Proximity Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|8=5.0

# Gyroscope - no rotation
Gyroscope Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|4=0.0:0.0:0.0

# Light sensor - moderate indoor lighting (100 lux)
Light Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|5=100.0
```

## Usage Instructions

### Step 1: Create the File

1. Create a text file named `sensor_data.txt`
2. Add your sensor configurations using the format described above
3. You can use the provided `sensor_data_example.txt` as a template

### Step 2: Copy to Device

Copy the file to your Android device at:
```
/sdcard/SensorDisabler/sensor_data.txt
```

You can use:
- ADB: `adb push sensor_data.txt /sdcard/SensorDisabler/`
- File manager app on the device
- USB connection with file transfer

### Step 3: Enable the Feature

In the Sensor Disabler app:
1. Open Settings
2. Enable "Use File Data" option
3. The app will now read sensor values from the file

### Step 4: Apply Changes

- Reboot the device, or
- Restart the target application that you want to affect

## Fallback Behavior

If file-based data reading is enabled but:
- The file doesn't exist
- The file cannot be read
- A specific sensor is not found in the file

The module will automatically fall back to using the values configured through the app's UI (stored in SharedPreferences).

## Troubleshooting

### File Not Being Read

1. Check that the file is at the correct location: `/sdcard/SensorDisabler/sensor_data.txt`
2. Verify file permissions (the file should be readable)
3. Check Android logs for error messages:
   ```
   adb logcat | grep SensorDataFileReader
   ```

### Sensor Keys Not Matching

1. Make sure there are no extra spaces in the sensor key
2. Verify the exact sensor name, vendor, version, and type from your device
3. Use the exact separator: `|*^&SensorDisabler&^*|`

### Values Not Applied

1. Ensure the target app is restarted after changing the file
2. Check that the sensor is configured to "Mock Values" mode in the app
3. Verify the number of values matches what the sensor expects

## Performance Considerations

- The file is read each time sensor values are requested
- For better performance with large files, consider:
  - Only including sensors you actually want to mock
  - Removing unnecessary comments from the file
  - Keeping the file size reasonable

## Security Note

The file is stored in external storage, which means:
- Other apps with storage permission can read it
- The file may be included in device backups
- Be careful not to include sensitive information in the file

## Integration with Existing Features

This feature works alongside existing Sensor Disabler features:
- **App Filtering**: File-based values respect allow/deny lists
- **Sensor Status**: Sensors must still be set to "Mock Values" mode
- **UI Configuration**: UI-configured values serve as fallback
- **Tasker Integration**: Compatible with Tasker automation

## API for Developers

If you're integrating this feature programmatically:

### Kotlin
```kotlin
val prefs = context.getSensorPreferences()
prefs.setFileDataEnabled(true)
```

### Java
```java
SharedPreferences prefs = SensorDataFileReader.getSharedPreferences(context);
prefs.edit().putBoolean(Constants.PREFS_KEY_USE_FILE_DATA, true).apply();
```

### Reading File Data Directly
```java
Map<String, float[]> sensorData = SensorDataFileReader.readSensorDataFromFile(
    context, 
    Constants.SENSOR_DATA_FILE_NAME
);
```

## Example Use Cases

### 1. Development/Testing
Quickly switch between different sensor configurations for testing apps.

### 2. Privacy
Set all sensors to neutral values to prevent fingerprinting.

### 3. Battery Saving
Mock sensors with constant values to reduce sensor hardware usage.

### 4. Accessibility
Configure sensors for specific accessibility needs.

### 5. Multi-Device Setup
Use the same sensor configuration across multiple devices by copying the file.

## See Also

- `sensor_data_example.txt` - Example configuration file
- `SensorDataFileReader.java` - Implementation details
- Main README.md - General Sensor Disabler documentation

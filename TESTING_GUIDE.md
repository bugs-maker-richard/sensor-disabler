# Testing Guide for Custom Sensor Data File Feature

This guide provides instructions for manually testing the new custom sensor data file feature.

## Prerequisites

- Device with Xposed Framework installed
- Sensor Disabler module installed and activated
- ADB access to the device (optional, for easier file management)
- A test app that uses sensors (e.g., any app that uses accelerometer or proximity sensor)

## Test Scenarios

### Test 1: Basic File Reading

**Objective**: Verify that the module can read sensor data from a text file.

**Steps**:
1. Create a test file `sensor_data.txt` with valid sensor configurations
2. Copy the file to `/sdcard/SensorDisabler/sensor_data.txt` on the device
3. In Sensor Disabler app, enable "Use File Data" option
4. Set a sensor to "Mock Values" mode
5. Restart a test app that uses the sensor
6. Verify the sensor returns the values specified in the file

**Expected Result**: The sensor returns the mock values from the file.

**Verification**:
```bash
# Check logs for file reading confirmation
adb logcat | grep SensorDataFileReader
```

Look for: `"Successfully loaded X sensor configurations from sensor_data.txt"`

---

### Test 2: Fallback to SharedPreferences

**Objective**: Verify that the module falls back to UI-configured values when the file is not available.

**Steps**:
1. Ensure no `sensor_data.txt` file exists on the device
2. Configure a sensor through the app UI with specific mock values
3. Enable "Use File Data" option
4. Set the sensor to "Mock Values" mode
5. Restart a test app that uses the sensor
6. Verify the sensor returns the UI-configured values

**Expected Result**: The sensor returns the values configured through the UI.

---

### Test 3: File Format Validation

**Objective**: Verify that the module handles various file formats correctly.

**Test Cases**:

#### 3a. Valid Format
```
# Comment line
SensorKey=1.0:2.0:3.0
```
**Expected**: Values are read successfully

#### 3b. Empty Lines
```
SensorKey=1.0:2.0:3.0

AnotherKey=4.0:5.0
```
**Expected**: Empty lines are ignored, both sensors are loaded

#### 3c. Comments
```
# This is a comment
SensorKey=1.0:2.0:3.0  # Inline comment not supported, this will fail
```
**Expected**: Line with inline comment should be skipped with a warning

#### 3d. Invalid Values
```
SensorKey=abc:def
```
**Expected**: Line is skipped with a warning in logs

#### 3e. Missing Separator
```
SensorKey 1.0:2.0:3.0
```
**Expected**: Line is skipped with a warning in logs

---

### Test 4: File Permissions

**Objective**: Verify proper handling of file permission issues.

**Steps**:
1. Create `sensor_data.txt` file
2. Change file permissions to make it unreadable: `chmod 000 /sdcard/SensorDisabler/sensor_data.txt`
3. Enable "Use File Data" option
4. Restart a test app
5. Check logs for error messages

**Expected Result**: Warning in logs, fallback to SharedPreferences values

**Cleanup**: `chmod 644 /sdcard/SensorDisabler/sensor_data.txt`

---

### Test 5: Sensor Key Matching

**Objective**: Verify that sensor keys must match exactly.

**Steps**:
1. Get the exact sensor key for a specific sensor from the device
2. Create a file with:
   - One entry with the correct sensor key
   - One entry with a slightly modified sensor key (e.g., extra space)
3. Enable "Use File Data"
4. Test both sensors

**Expected Result**: 
- Sensor with correct key uses file values
- Sensor with incorrect key uses SharedPreferences values

---

### Test 6: Multiple Sensors

**Objective**: Verify that multiple sensors can be configured simultaneously.

**Steps**:
1. Create a file with configurations for 3+ different sensors:
   ```
   AccelerometerKey=0.0:0.0:9.8
   ProximityKey=5.0
   GyroscopeKey=0.0:0.0:0.0
   ```
2. Copy file to device
3. Enable "Use File Data"
4. Set all configured sensors to "Mock Values" mode
5. Test an app that uses multiple sensors

**Expected Result**: All sensors return their configured values from the file.

---

### Test 7: File Update Without Restart

**Objective**: Verify behavior when file is modified while app is running.

**Steps**:
1. Create initial `sensor_data.txt` with one set of values
2. Enable "Use File Data" and test
3. Modify the file with different values (without restarting device)
4. Force-stop and restart the test app
5. Verify if new values are used

**Expected Result**: New values should be used after app restart (file is read on-demand).

---

### Test 8: Integration with App Filtering

**Objective**: Verify that file-based values respect app filtering settings.

**Steps**:
1. Configure file with sensor values
2. Enable "Use File Data"
3. Configure app filtering (either Allow or Deny list)
4. Add a test app to the filter list
5. Test both filtered and non-filtered apps

**Expected Result**: File-based values only apply to apps that are not filtered out.

---

### Test 9: Performance Test

**Objective**: Verify that file reading doesn't cause noticeable performance issues.

**Steps**:
1. Create a large file with 50+ sensor configurations
2. Enable "Use File Data"
3. Test app with high sensor sampling rate
4. Monitor app responsiveness

**Expected Result**: No noticeable lag or performance degradation.

---

### Test 10: Disable File Data Feature

**Objective**: Verify that disabling the feature reverts to normal behavior.

**Steps**:
1. Enable "Use File Data" and verify it works
2. Disable "Use File Data" option
3. Restart test app
4. Verify sensors use UI-configured values only

**Expected Result**: File is no longer read, UI values are used.

---

## Testing with ADB Commands

### Useful ADB Commands

```bash
# Push test file to device
adb push sensor_data.txt /sdcard/SensorDisabler/

# Pull file from device for verification
adb pull /sdcard/SensorDisabler/sensor_data.txt

# View file content on device
adb shell cat /sdcard/SensorDisabler/sensor_data.txt

# Check file permissions
adb shell ls -l /sdcard/SensorDisabler/sensor_data.txt

# Monitor logs for SensorDataFileReader
adb logcat | grep SensorDataFileReader

# Monitor logs for sensor activity
adb logcat | grep -E "SensorDataFileReader|SensorModificationMethod"

# Clear app data (to reset settings)
adb shell pm clear com.mrchandler.disableprox

# Restart test app
adb shell am force-stop <package.name>
adb shell am start -n <package.name>/.MainActivity
```

### Creating Test Sensor Keys

To generate proper sensor keys for testing:

```bash
# Get sensor list from device
adb shell dumpsys sensorservice

# Or check app logs when opening sensor details
adb logcat | grep SensorUtil
```

## Expected Log Messages

### Successful File Reading
```
D/SensorDataFileReader: Loaded sensor data for key: [SensorKey] with X values
I/SensorDataFileReader: Successfully loaded X sensor configurations from sensor_data.txt
```

### File Not Found
```
W/SensorDataFileReader: Sensor data file does not exist or cannot be read: /storage/emulated/0/SensorDisabler/sensor_data.txt
```

### Parse Errors
```
W/SensorDataFileReader: Invalid line format at line X: [line content]
W/SensorDataFileReader: Invalid values at line X: [values]
```

### File Read Error
```
E/SensorDataFileReader: Error reading sensor data file: [path]
```

## Common Issues and Solutions

### Issue: File not being read
**Solution**: 
- Check file path is exactly `/sdcard/SensorDisabler/sensor_data.txt`
- Verify "Use File Data" is enabled in settings
- Check file permissions with `adb shell ls -l`

### Issue: Values not applied
**Solution**:
- Ensure sensor is set to "Mock Values" mode in the app
- Restart the target application
- Verify sensor key matches exactly (no extra spaces)

### Issue: Some sensors work, others don't
**Solution**:
- Check logs for parse errors for specific sensors
- Verify each sensor key is correct
- Ensure value count matches sensor expectations

### Issue: File changes not reflected
**Solution**:
- Restart the target application (not just the device)
- File is read on-demand, so may need app restart
- Check if file was actually modified on device

## Reporting Test Results

When reporting test results, please include:
1. Test scenario number and name
2. Device model and Android version
3. Xposed Framework version
4. Sensor Disabler version
5. Test result (Pass/Fail)
6. Relevant log excerpts
7. Steps to reproduce any failures

## Automated Testing (Future Enhancement)

For future releases, consider implementing:
- Unit tests for SensorDataFileReader
- Integration tests for file reading with mock sensors
- Performance benchmarks for file reading
- Automated UI tests for enabling/disabling the feature

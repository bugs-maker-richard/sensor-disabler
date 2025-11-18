# Quick Start: Custom Sensor Data Files

## 快速开始指南 / Quick Start Guide

This guide helps you quickly set up and use the custom sensor data file feature.

## 5分钟设置 / 5-Minute Setup

### 步骤 1: 创建配置文件 / Step 1: Create Configuration File

Create a file named `sensor_data.txt` on your computer:

```
# My sensor configuration
# Accelerometer - device laying flat
Accelerometer Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|1=0.0:0.0:9.8

# Proximity - 5cm from object
Proximity Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|8=5.0
```

### 步骤 2: 上传到设备 / Step 2: Upload to Device

Using ADB:
```bash
adb push sensor_data.txt /sdcard/SensorDisabler/
```

Or use a file manager app to copy the file to:
```
/sdcard/SensorDisabler/sensor_data.txt
```

### 步骤 3: 启用功能 / Step 3: Enable Feature

1. Open Sensor Disabler app
2. Go to Settings
3. Enable "Use File Data" option

### 步骤 4: 配置传感器 / Step 4: Configure Sensors

1. Select the sensors you want to mock
2. Set them to "Mock Values" mode
3. The values from the file will be used

### 步骤 5: 重启应用 / Step 5: Restart Apps

Restart the target application(s) you want to affect.

That's it! Your sensors will now use the values from the file.

---

## 获取传感器键 / Getting Sensor Keys

The tricky part is getting the correct sensor keys. Here's how:

### 方法 1: 使用应用 / Method 1: Using the App

1. Open Sensor Disabler
2. View the list of sensors
3. Tap on a sensor to see its details
4. Note down:
   - Name (e.g., "Accelerometer Sensor")
   - Vendor (e.g., "Google Inc.")
   - Version (e.g., "1")
   - Type (e.g., "1")

5. Combine them with the separator:
   ```
   Name|*^&SensorDisabler&^*|Vendor|*^&SensorDisabler&^*|Version|*^&SensorDisabler&^*|Type
   ```

### 方法 2: 使用 ADB / Method 2: Using ADB

```bash
# View sensor information
adb shell dumpsys sensorservice
```

Look for sensor details in the output and construct the key.

### 方法 3: 从日志 / Method 3: From Logs

```bash
# Monitor logs when opening sensor details
adb logcat | grep SensorUtil
```

The sensor key will be logged when you interact with sensors in the app.

---

## 常见配置示例 / Common Configuration Examples

### 示例 1: 静止设备 / Example 1: Stationary Device

```
# Device laying flat on table
Accelerometer Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|1=0.0:0.0:9.8
Gyroscope Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|4=0.0:0.0:0.0
```

### 示例 2: 黑暗环境 / Example 2: Dark Environment

```
# Very low light
Light Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|5=0.5
```

### 示例 3: 距离感应 / Example 3: Proximity Detection

```
# Far from any object
Proximity Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|8=10.0
```

### 示例 4: 隐私保护 / Example 4: Privacy Protection

```
# Neutral values for all sensors
Accelerometer Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|1=0.0:0.0:0.0
Gyroscope Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|4=0.0:0.0:0.0
Magnetic Field Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|2=0.0:0.0:0.0
Light Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|5=100.0
Proximity Sensor|*^&SensorDisabler&^*|Google Inc.|*^&SensorDisabler&^*|1|*^&SensorDisabler&^*|8=5.0
```

---

## 故障排除 / Troubleshooting

### 问题: 文件未读取 / Issue: File Not Being Read

✅ **检查清单 / Checklist:**
- [ ] File is at `/sdcard/SensorDisabler/sensor_data.txt`
- [ ] "Use File Data" is enabled in settings
- [ ] Sensor is set to "Mock Values" mode
- [ ] Target app has been restarted

🔍 **调试 / Debug:**
```bash
# Check if file exists
adb shell ls -l /sdcard/SensorDisabler/sensor_data.txt

# Check logs
adb logcat | grep SensorDataFileReader
```

### 问题: 传感器键不匹配 / Issue: Sensor Key Not Matching

✅ **解决方法 / Solution:**
- Make sure there are no extra spaces
- Use the exact separator: `|*^&SensorDisabler&^*|`
- Verify name, vendor, version, and type are correct

### 问题: 值未应用 / Issue: Values Not Applied

✅ **解决方法 / Solution:**
- Restart the target app (not just switch apps)
- Make sure the sensor is in "Mock Values" mode
- Check that the number of values matches the sensor

---

## 文件格式规则 / File Format Rules

### ✅ 有效格式 / Valid Format

```
# This is a comment
SensorKey=1.0:2.0:3.0
```

### ❌ 无效格式 / Invalid Format

```
# Missing separator
SensorKey 1.0:2.0:3.0

# Invalid values
SensorKey=abc:def

# Missing values
SensorKey=

# Inline comments not supported
SensorKey=1.0:2.0 # comment
```

---

## 高级提示 / Advanced Tips

### 提示 1: 使用模板 / Tip 1: Use Templates

Create template files for different scenarios:
- `sensor_data_stationary.txt` - Device not moving
- `sensor_data_dark.txt` - Low light environment
- `sensor_data_privacy.txt` - Privacy-focused values

### 提示 2: 设备间共享 / Tip 2: Share Between Devices

Export your configuration and share with other devices:
```bash
# Export from device 1
adb pull /sdcard/SensorDisabler/sensor_data.txt

# Import to device 2
adb push sensor_data.txt /sdcard/SensorDisabler/
```

### 提示 3: 备份配置 / Tip 3: Backup Configuration

```bash
# Backup
adb pull /sdcard/SensorDisabler/sensor_data.txt backup_$(date +%Y%m%d).txt

# Restore
adb push backup_20240101.txt /sdcard/SensorDisabler/sensor_data.txt
```

### 提示 4: 性能优化 / Tip 4: Performance Optimization

- Only include sensors you actually want to mock
- Remove unnecessary comments from the file
- Keep the file size reasonable

---

## 需要帮助？ / Need Help?

📖 **详细文档 / Detailed Documentation:**
- [SENSOR_DATA_FILE.md](SENSOR_DATA_FILE.md) - Complete feature documentation
- [TESTING_GUIDE.md](TESTING_GUIDE.md) - Testing scenarios
- [sensor_data_example.txt](sensor_data_example.txt) - Example file

🐛 **报告问题 / Report Issues:**
- Check logs: `adb logcat | grep SensorDataFileReader`
- Include device model and Android version
- Provide the sensor_data.txt file (if not sensitive)

---

## 总结 / Summary

| 步骤 / Step | 操作 / Action |
|------------|--------------|
| 1 | Create `sensor_data.txt` file |
| 2 | Copy to `/sdcard/SensorDisabler/` |
| 3 | Enable "Use File Data" in settings |
| 4 | Set sensors to "Mock Values" mode |
| 5 | Restart target apps |

**文件格式 / File Format:**
```
SensorKey=value1:value2:value3
```

**位置 / Location:**
```
/sdcard/SensorDisabler/sensor_data.txt
```

Enjoy your custom sensor configurations! 🎉

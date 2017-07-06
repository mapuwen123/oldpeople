package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfo {

    // 设备类型
    public final static int DEVICE_TYPE_BODYFAT = 6;// ：体脂
    public final static int DEVICE_TYPE_BLOODPRESSURE = 1;// ：血压
    public final static int DEVICE_TYPE_BLOODSUGER = 2;// ：血糖
    public final static int DEVICE_TYPE_BLOODOXYGEN = 4;// ：血氧
    public final static int DEVICE_TYPE_EGC = 5;// ：心电
    public final static int DEVICE_TYPE_TEMP = 3;// 温度计

    public int mDeviceType;
    public String mDeviceName;

    public DeviceInfo() {
    }

    public DeviceInfo(JSONObject object) throws JSONException {
        if (object == null) {
            return;
        }
        if (object.has("type")) {
            mDeviceType = object.getInt("type");
        }
        if (object.has("deviceName")) {
            mDeviceName = object.getString("deviceName");
        }

    }

    public JSONObject objToJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("type", mDeviceType);
        object.put("deviceName", mDeviceName);
        return object;
    }
}

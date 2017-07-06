/**
 * 健康指数
 */
package com.aiminerva.oldpeople.bean;

import com.aiminerva.oldpeople.globalsettings.GlobalSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class HealthIndicesInfo {

    public static final int HEALTH_TYPE_HEART = 5;// 心电 201 EGC
    public static final int HEALTH_TYPE_BODYFAT = 6;// 体质 301
    public static final int HEALTH_TYPE_BLOODPRESS = 1; // 血压 503
    public static final int HEALTH_TYPE_BLOODSUGER = 2;// 血糖 601
    public static final int HEALTH_TYPE_OXGEN = 4;// 血氧 801
    public static final int HEALTH_TYPE_TEMP = 3;// 体温 ？

    // base info
    public long mDate;
    public String mUDID;
    public String mUserId;
    public int mValueType;
    public int mHasSent;
    public String mMsgId;

    public HealthIndicesInfo() {
        mDate = System.currentTimeMillis();
        mUserId = GlobalSettings.getInstance().mCurrentRelationMember;
        mHasSent = -1;
    }

    public HealthIndicesInfo(String jsonStream) throws JSONException {

        // if (jsonStream == null || jsonStream.isEmpty()) {
        // return;
        // }

        JSONObject object;
        object = new JSONObject(jsonStream);

        mUserId = object.has("userid") ? object.getString("userid") : "";
        mDate = object.has("time") ? object.getLong("time") : 0;
        mValueType = object.has("type") ? object.getInt("type") : -1;
    }

    public JSONObject objectToJsonStream() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", mUserId);
        jsonObject.put("time", mDate);
        jsonObject.put("type", mValueType);

        return jsonObject;
    }
}

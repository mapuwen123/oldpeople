package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BloodPressInfo extends HealthIndicesInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8171233331417715687L;
    public int mSystolicPressure;//收缩压
    public int mDiastolicPressure;//舒张压
    public int mPluse;//脉搏数/分
    public int level=-1;

    public BloodPressInfo() {
    }

    public BloodPressInfo(String jsonStream) throws JSONException {
        super(jsonStream);

        JSONObject object = new JSONObject(jsonStream);
        if (object.has("systolicPress")) {
            mSystolicPressure = object.getInt("systolicPress");
        }
        if (object.has("diastolicpress")) {
            mDiastolicPressure = object.getInt("diastolicpress");
        }
        if (object.has("plusstate")) {
            mPluse = object.getInt("plusstate");
        }
    }

    @Override
    public JSONObject objectToJsonStream() throws JSONException {
        // TODO Auto-generated method stub
        JSONObject jsonObject = super.objectToJsonStream();
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }

        jsonObject.put("systolicPressure", mSystolicPressure);
        jsonObject.put("diastolicPressure", mDiastolicPressure);
        jsonObject.put("pluse", mPluse);

        return jsonObject;
    }
}

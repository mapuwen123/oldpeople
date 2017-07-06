package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BloodSugerInfo extends HealthIndicesInfo implements Serializable {

    //public int mTime;
//	空腹：3.9-7       餐后两小时：3.9-10
//	level=1 偏高 level=2 偏低 level=0 正常

    public float mBloodSuger;
    public int mTypebloodSuger;
    public int level;

    public BloodSugerInfo() {
    }

    public BloodSugerInfo(String jsonStream) throws JSONException {
        super(jsonStream);

        JSONObject object = new JSONObject(jsonStream);
        if (object.has("bloodSuger")) {
            double value = object.getDouble("bloodSuger");
            mBloodSuger = (float) ((long) Math.round(value * 10) / 10.0);//保留小数点后一位
        }
        if (object.has("typebloodSuger")) {
            mTypebloodSuger = object.getInt("typebloodSuger");
        }
    }

    @Override
    public JSONObject objectToJsonStream() throws JSONException {
        // TODO Auto-generated method stub
        JSONObject jsonObject = super.objectToJsonStream();
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        jsonObject.put("bloodsuger", mBloodSuger);
        jsonObject.put("typebloodSuger", mTypebloodSuger);

        return jsonObject;
    }
}

package com.aiminerva.oldpeople.bean;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BodyFatInfo extends HealthIndicesInfo implements Serializable {

    public static class EnumHabitusLevelStatus {// 体型
        public static int HABITUSLEVEL_THIN = 0x01;
        public static int HABITUSLEVEL_NORMAL = 0x02;
        public static int HABITUSLEVEL_INVISIBLE_OBESITY = 0x03;
        public static int HABITUSLEVEL_ROBUST = 0x04;
        public static int HABITUSLEVEL_OBESITY = 0x05;

        @SuppressLint("UseSparseArrays")
        @SuppressWarnings("serial")
        public static Map<Integer, String> HABITUSLEVEL_MAP = new HashMap<Integer, String>() {
            {
                put(HABITUSLEVEL_THIN, "消瘦");
                put(HABITUSLEVEL_NORMAL, "标准");
                put(HABITUSLEVEL_INVISIBLE_OBESITY, "隐藏性肥胖");
                put(HABITUSLEVEL_ROBUST, "健壮");
                put(HABITUSLEVEL_OBESITY, "肥胖");
            }
        };
    }

    public static class EnumBMILevelStatus {// 体质
        public static int BMILEVEL_LITTLE_LOW = 0x01;
        public static int BMILEVEL_NORMAL = 0x02;
        public static int BMILEVEL_LITTLE_HIGH = 0x03;
        public static int BMILEVEL_HIGHT = 0x04;

        @SuppressLint("UseSparseArrays")
        @SuppressWarnings("serial")
        public static Map<Integer, String> BMILEVEL_MAP = new HashMap<Integer, String>() {
            {
                put(BMILEVEL_LITTLE_LOW, "偏低");
                put(BMILEVEL_NORMAL, "标准");
                put(BMILEVEL_LITTLE_HIGH, "偏高");
                put(BMILEVEL_HIGHT, "高");
            }
        };

    }

    /**
     *
     */
    private static final long serialVersionUID = -5796244498757255312L;
    public int mHeight;// 身高
    public int mWeight;// 体重
    public int mYear;// 年龄
    public int mSex;// 性别
    // public long mTime;// 测量时间
    public float mFatPercent;// 体质比例
    public float mBMIValue;// 体脂水平
    public int mBasalMetabolism;// 基础代谢值
    public int mBMILevel;// 体质水平
    public int mHabitusLevel;// 体型水平public
    public int mWater;

    public BodyFatInfo() {
    }

    public BodyFatInfo(String jsonStream) throws JSONException {
        super(jsonStream);

        if (jsonStream == null || jsonStream.isEmpty()) {
            return;
        }

        JSONObject object = new JSONObject(jsonStream);
        if (object.has("height")) {
            mHeight = object.getInt("height");
        }
        if (object.has("weight")) {
            mWeight = object.getInt("weight");
        }
        if (object.has("year")) {
            mYear = object.getInt("year");
        }
        if (object.has("sex")) {
            mSex = object.getInt("sex");
        }
        if (object.has("fatPercent")) {
            mFatPercent = (float) object.getDouble("fatPercent");
        }
        if (object.has("bmi")) {
            mBMIValue = (float) object.getDouble("bmi");
        }
        if (object.has("basalMetabolism")) {
            mBasalMetabolism = object.getInt("basalMetabolism");
        }
        if (object.has("bmiLevel")) {
            mBMILevel = object.getInt("bmiLevel");
        }
        if (object.has("habitusLevel")) {
            mHabitusLevel = object.getInt("habitusLevel");
        }
    }

    @Override
    public JSONObject objectToJsonStream() throws JSONException {

        JSONObject jsonObject = super.objectToJsonStream();

        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }

        jsonObject.put("height", mHeight);
        jsonObject.put("weight", mWeight);
        jsonObject.put("year", mYear);
        jsonObject.put("sex", mSex);
        jsonObject.put("facPercent", mFatPercent);
        jsonObject.put("bmi", mBMIValue);
        jsonObject.put("basalMetabolism", mBasalMetabolism);
        jsonObject.put("bmiLevel", mBMILevel);
        jsonObject.put("habitusLevel", mHabitusLevel);

        return jsonObject;
    }

}

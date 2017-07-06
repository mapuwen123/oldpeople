package com.aiminerva.oldpeople.bean;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FinltopInfo {

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, List<String>> mFinltops = new HashMap<Integer, List<String>>();

    public FinltopInfo() {
    }

    //获取心电设备mac地址列表
    public List<String> getFinltop2() {
        List<String> finltops = mFinltops.get(2);
        if (finltops != null) {
            return finltops;
        }
        return new ArrayList<String>();
    }

    public boolean hasFinltop2Mac(String mac) {
        List<String> finltop2Macs = mFinltops.get(2);
        if (finltop2Macs == null) {
            return false;
        }
        for (String finltop : finltop2Macs) {
            if (finltop.equals(mac)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFinltop2Mac() {
        List<String> finltop2Macs = mFinltops.get(2);
        if (finltop2Macs == null || finltop2Macs.size() == 0) {
            return false;
        }
        return true;
    }

    public void setFinltop2(List<String> finltop2) {
        mFinltops.put(2, finltop2);
    }

    //获取血压设备mac地址列表
    public List<String> getFinltop3() {
        List<String> finltops = mFinltops.get(3);
        if (finltops != null) {
            return finltops;
        }
        return new ArrayList<String>();
    }

    public boolean hasFinltop3Mac(String mac) {
        List<String> finltop3Macs = mFinltops.get(3);
        if (finltop3Macs == null) {
            return false;
        }
        for (String finltop : finltop3Macs) {
            if (finltop.equals(mac)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFinltop3Mac() {
        List<String> finltop3Macs = mFinltops.get(3);
        if (finltop3Macs == null || finltop3Macs.size() == 0) {
            return false;
        }
        return true;
    }

    public void setFinltop3(List<String> finltop3) {
        mFinltops.put(3, finltop3);
    }

    public FinltopInfo(String jsonStream) throws JSONException {
        if (jsonStream == null || jsonStream.isEmpty()) {
            return;
        }
        JSONObject object;

        object = new JSONObject(jsonStream);

        List<String> finltop_2Array = new ArrayList<String>();

        JSONArray objects = null;
        try {
            objects = object.getJSONArray("finltop_2");
        } catch (JSONException e) {
            //TODO
            objects = null;
        }

        if (objects != null) {
            for (int i = 0; i < objects.length(); i++) {
                finltop_2Array.add(objects.getString(i));
            }
        }

        List<String> finltop_3Array = new ArrayList<String>();

        try {
            objects = object.getJSONArray("finltop_3");
        } catch (JSONException e) {
            //TODO
            objects = null;
        }

        if (objects != null) {
            for (int i = 0; i < objects.length(); i++) {
                finltop_3Array.add(objects.getString(i));
            }
        }

        mFinltops.put(2, finltop_2Array);// finltop 2 mac list
        mFinltops.put(3, finltop_3Array);// finltop 3 mac list
    }

    public String objectToJsonStream() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray objects;
        List<String> finltop_2Array = mFinltops.get(2);
        if (finltop_2Array != null) {
            objects = new JSONArray();
            for (int i = 0; i < finltop_2Array.size(); i++) {
                objects.put(finltop_2Array.get(i));
            }
            jsonObject.put("finltop_2", objects);
        }

        List<String> finltop_3Array = mFinltops.get(3);
        if (finltop_3Array != null) {
            objects = new JSONArray();
            for (int i = 0; i < finltop_3Array.size(); i++) {
                objects.put(finltop_3Array.get(i));
            }
            jsonObject.put("finltop_3", objects);

        }

        return jsonObject.toString();
    }

}

package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuInfo {
    public int mMenuIndex;
    public String mMenuTitle;

    public MenuInfo() {
    }

    public MenuInfo(JSONObject object) throws JSONException {
        if (object == null) {
            return;
        }
        if (object.has("id")) {
            mMenuIndex = object.getInt("id");
        }
        if (object.has("title")) {
            mMenuTitle = object.getString("title");
        }
    }

    public JSONObject objectToJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", mMenuIndex);
        object.put("title", mMenuTitle);
        return object;
    }
}

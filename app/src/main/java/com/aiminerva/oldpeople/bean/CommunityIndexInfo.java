package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class CommunityIndexInfo {

    public String mCommunityid = "";
    public String mCommunityname = "";

    public CommunityIndexInfo() {
    }

    public CommunityIndexInfo(JSONObject object) throws JSONException {
        if (object == null) {
            return;
        }
        if (object.has("orgid")) {
            mCommunityid = object.getString("orgid");
        }
        if (object.has("orgname")) {
            mCommunityname = object.getString("orgname");
        }
    }

    public JSONObject objectToJsonStream() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("orgid", mCommunityid);
        jsonObject.put("orgname", mCommunityname);
        return jsonObject;
    }
}

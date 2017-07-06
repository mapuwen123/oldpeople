package com.aiminerva.oldpeople.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberInfo {

    public String mMemberId;
    public String mMemberName;
    public String mImageUrl;

    public MemberInfo() {
    }

    public MemberInfo(JSONObject object) throws JSONException {
        if (object == null) {
            return;
        }
        if (object.has("id")) {
            mMemberId = object.get("id").toString();
        }
        if (object.has("familyName")) {
            mMemberName = object.getString("familyName");
        }
        if (object.has("imageUrl")) {
            mImageUrl = object.getString("imageUrl");
        }
    }

    public JSONObject objectToJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", mMemberId);
        object.put("familyName", mMemberName);
        object.put("imageUrl", mImageUrl);
        return object;
    }
}

package com.aiminerva.oldpeople.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    // 判断是否自动登录的表示 0表示手动登录 1表示自动登录
    public String mIsAutomaticLogin;
    public String mUserId;
    public String mPhone;
    public String mUserName;
    public String mShowCover;// 头像地址url
    public String mName;
    public String mToken;
    public String mAge;
    public String mGender;
    public String mAddress;
    public String mBloodType;
    public String mRoleID = "";
    public String mSessionid;
    public CommunityIndexInfo mCommunity;

    public List<MenuInfo> mMenuList;
    public List<MemberInfo> mMemberList;
    public List<DeviceInfo> mDeviceList;
    // add
    public String mPassword;
    public Boolean mChecked;

    public UserInfo() {
    }

    public UserInfo(String jsonStream) throws JSONException {
        if (jsonStream == null || jsonStream.isEmpty()) {
            return;
        }
        JSONObject object;

        object = new JSONObject(jsonStream);
        if (object.has("userid")) {
            mUserId = object.getString("userid");
        }
        // mPhone = object.getString("phone");
        if (object.has("userName")) {
            mUserName = object.getString("userName");
        }
        if (object.has("showCover")) {
            mShowCover = object.getString("showCover");
        }
        if (object.has("name")) {
            mName = object.getString("name");
        }
        if (object.has("age")) {
            mAge = object.getString("age");
        }
        if (object.has("gender")) {
            mGender = object.getString("gender");
        }
        if (object.has("address")) {
            mAddress = object.getString("address");
        }
        if (object.has("bloodtype")) {
            mBloodType = object.getString("bloodtype");
        }
        if (object.has("isAutomaticLogin")) {
            mIsAutomaticLogin = object.getString("isAutomaticLogin");
        }
        if (object.has("token")) {
            mToken = object.getString("token");
        } else {
            mToken = "";
        }

        if (object.has("sessionid")) {
            mSessionid = object.getString("sessionid");
        } else {
            mSessionid = "";
        }

        if (object.has("roleid")) {
            mRoleID = object.getString("roleid");
        } else {
            mRoleID = "";
        }

        if (object.has("password")) {
            mPassword = object.getString("password");
        } else {
            mPassword = "";
        }
        if (object.has("checked")) {
            mChecked = object.getBoolean("checked");
        } else {
            mChecked = false;
        }

        if (object.has("community")) {
            mCommunity = new CommunityIndexInfo(
                    object.getJSONObject("community"));
        } else {
            mCommunity = new CommunityIndexInfo();
        }

        List<MenuInfo> menuList = new ArrayList<MenuInfo>();
        JSONArray objects = object.getJSONArray("menuList");
        for (int i = 0; i < objects.length(); i++) {
            MenuInfo menu = new MenuInfo(objects.getJSONObject(i));
            menuList.add(menu);
        }
        if (menuList != null) {
            mMenuList = menuList;
        }

        List<MemberInfo> memberList = new ArrayList<MemberInfo>();
        objects = object.getJSONArray("memberList");

        for (int i = 0; i < objects.length(); i++) {

            MemberInfo member = new MemberInfo(objects.getJSONObject(i));
            memberList.add(member);
        }

        if (memberList != null) {
            mMemberList = memberList;
        }

        List<DeviceInfo> deviceInfos = new ArrayList<DeviceInfo>();
        objects = object.getJSONArray("deviceList");

        for (int i = 0; i < objects.length(); i++) {
            DeviceInfo device = new DeviceInfo(objects.getJSONObject(i));
            deviceInfos.add(device);
        }
        if (deviceInfos != null) {
            mDeviceList = deviceInfos;
        }
    }

    public String objectToJsonStream() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", mUserId);
        // jsonObject.put("phone", mPhone);
        jsonObject.put("userName", mUserName);
        jsonObject.put("showCover", mShowCover);
        jsonObject.put("name", mName);
        jsonObject.put("age", mAge);
        jsonObject.put("gender", mGender);
        jsonObject.put("address", mAddress);
        jsonObject.put("bloodtype", mBloodType);
        jsonObject.put("token", mToken);
        jsonObject.put("isAutomaticLogin", mIsAutomaticLogin);
        jsonObject.put("sessionid", mSessionid);
        jsonObject.put("roleid", mRoleID);


        if (mPassword != null) {
            jsonObject.put("password", mPassword);
        }

        jsonObject.put("checked", mChecked);

        if (mCommunity != null) {
            jsonObject.put("community", mCommunity.objectToJsonStream());
        }
        JSONArray objects = new JSONArray();
        for (int i = 0; i < mMenuList.size(); i++) {
            MenuInfo menuInfo = mMenuList.get(i);
            JSONObject object = menuInfo.objectToJson();
            objects.put(object);
        }
        jsonObject.put("menuList", objects);

        objects = new JSONArray();
        for (int i = 0; i < mMemberList.size(); i++) {
            MemberInfo memberInfo = mMemberList.get(i);
            JSONObject object = memberInfo.objectToJson();
            objects.put(object);
        }
        jsonObject.put("memberList", objects);

        objects = new JSONArray();
        for (int i = 0; i < mDeviceList.size(); i++) {
            DeviceInfo deviceInfo = mDeviceList.get(i);
            JSONObject object = deviceInfo.objToJson();
            objects.put(object);
        }
        jsonObject.put("deviceList", objects);

        return jsonObject.toString();
    }

}

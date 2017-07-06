package com.aiminerva.oldpeople.globalsettings;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.bean.CommunityIndexInfo;
import com.aiminerva.oldpeople.bean.FinltopInfo;
import com.aiminerva.oldpeople.bean.UserInfo;
import com.aiminerva.oldpeople.utils.SpUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 爱家医生常量新app可以忽略
 */
public class GlobalSettings {

    public static class EnumBloodSugerType {
        public static final int BLOODSUGER_FAST = 0x01;
        public static final int BLOODSUGER_AFTERMEAL = 0x02;
    }

    private static final String CACHE_VERSION = "1.0";
    public boolean mIsRefrence = false;// 是否自动刷新数据
    private final String TAG = "GlobalManager";

    private String mFileName = "Global";
    private Context mContext = MyApplication.getContext();

    private HashMap<String, Object> mCachesMap;
    public String mCurrentRelationMember;// 当前显示用户ID
    public String mSession = "session";// session
    public String mTargetId = "26594";// 接受消息人的Id
    public String mName = "常竹悦";// 接受消息人的姓名
    public String mUrl = "";// 头像地址
    public String mTargetRole = "0";// 接受消息人的角色 3是专家
    public String mOrderId = "0";//当前的订单id
    public int mReceipt = 0;//专家1接受2拒绝


    public List<CommunityIndexInfo> mCommunityList = new ArrayList<CommunityIndexInfo>();
    //public CommunityIndexInfo mCommunityInfo;
    // 1 代表空腹 2.餐后
    public int mBloodSugerType = EnumBloodSugerType.BLOODSUGER_AFTERMEAL;

    public String mQATitle;
    public String mQAContents;

//	public HashMap<Integer, String> mFT_DeviceMap = new HashMap<Integer, String>();

    private static GlobalSettings gGlobalManager;

    synchronized public static GlobalSettings getInstance() {
        if (gGlobalManager == null) {
            gGlobalManager = new GlobalSettings();
        }
        return gGlobalManager;
    }

    public void logout() {
        SpUtil.putString(MyApplication.getContext(), GlobalConstants.LOGIN_INFO, "");
    }

    private GlobalSettings() {

        mFileName = "Globel";

        String stream = this.readOutputStreamToCache();
        Log.e(TAG, "stream------" + stream);
        JSONObject object;
        try {

            if (!TextUtils.isEmpty(stream)) {
                object = new JSONObject(stream);

                Log.e(TAG, "object=======" + object);

                if (mCachesMap == null) {
                    mCachesMap = new HashMap<String, Object>();
                }

                if (object.has("Version") && object.getString("Version").equals(CACHE_VERSION)) {
                    mCachesMap.put("Version", object.get("Version"));
                    mCachesMap.put("UserInfo", object.get("UserInfo"));
                    mCachesMap.put("FinltopMap", object.get("FinltopMap"));
                } else {
                    mCachesMap.put("Version", CACHE_VERSION);
                    mCachesMap.put("UserInfo", object.get("UserInfo"));
                    mCachesMap.put("FinltopMap", object.get("FinltopMap") == null ? "" : object.get("FinltopMap"));
                    saveStringToDrafts(CACHE_VERSION, "Version");
                }

//				//FT 设备
//				mFT_DeviceMap.put(2, "");
//				mFT_DeviceMap.put(3, "");

            } else {
                if (mCachesMap == null) {
                    mCachesMap = new HashMap<String, Object>();
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCachesMap = new HashMap<String, Object>();
        }
    }

    public final UserInfo getUserInfo() {
        String response = SpUtil.getString(MyApplication.getContext(), GlobalConstants.LOGIN_INFO, "");
        if(response.equals("")){
            return null;
        }
        try {
            Gson gson = new Gson();
            UserInfo userinfo = gson.fromJson(response, UserInfo.class);
            return userinfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void updateHeadPic(final String headpic) {
        UserInfo userInfo =getUserInfo();
        userInfo.mShowCover=headpic;
        Gson gson = new Gson();
        String result = gson.toJson(userInfo);
        if(result!=null){
            SpUtil.putString(MyApplication.getContext(), GlobalConstants.LOGIN_INFO,result);
        }
    }

    public String getCacheVersion() {
        String jsonStream = (String) (this.mCachesMap.get("Version"));
        if (jsonStream == null || jsonStream.isEmpty()) {
            return null;
        }

        return jsonStream;
    }

    private String getString(JSONObject obj, String name) throws JSONException {
        if (obj.isNull(name))
            return "";
        return obj.getString(name);
    }

    private void setString(JSONObject obj, String name) throws JSONException {
        if (!obj.isNull(name)){
            obj.put(name,obj);
        }
    }



    /**
     * get finltop info
     *
     * @return
     */
    public FinltopInfo getFinltopInfo() {
        String jsonStream = (String) (this.mCachesMap.get("FinltopMap"));
        if (jsonStream == null || jsonStream.isEmpty()) {
            return new FinltopInfo();
        }
        if (jsonStream == null || jsonStream.isEmpty()) {
            return new FinltopInfo();
        }
        try {
            return new FinltopInfo(jsonStream);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new FinltopInfo();
    }

    /**
     * save finltop info
     *
     * @param finltop
     */
    public void saveFinltopInfo(final FinltopInfo finltop) {
        if (finltop != null) {
            String jsonStream = "";
            try {
                jsonStream = finltop.objectToJsonStream();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mCachesMap.put("FinltopMap", jsonStream);
            saveStringToDrafts(jsonStream, "FinltopMap");
        }
    }

    /**
     * save global object stream
     *
     * @param value
     * @param key
     */
    private void saveStringToDrafts(final String value, final String key) {

        if (value.isEmpty()) {
            this.mCachesMap.put(key, value);
        }

        JSONObject object = new JSONObject(this.mCachesMap);
        String caches = object.toString();
        this.writeInputStreamToCache(caches);
    }

    /**
     * get global object stream
     *
     * @param key
     * @return
     */
    private String getStringFromDrafts(final String key) {

        String stream = (String) (this.mCachesMap.get(key));
        return stream;
    }


    /**
     * read String from file
     *
     * @return
     */
    private String readOutputStreamToCache() {
        try {
            FileInputStream fInputStream = null;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            if (!(Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED))) {
                fInputStream = mContext.openFileInput(mFileName);
            } else {
                String cacheDir = this.getCacheDir();

                File readFile = new File(cacheDir, mFileName);
                if (readFile.exists() == false) {
                    boolean a = readFile.createNewFile();// 建立文件
                    if (a) {
                        System.out.println();
                    } else {
                        System.out.println();
                    }
                }
                fInputStream = new FileInputStream(readFile);
            }

            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            if (outStream.toString() == null) {
                return "";
            }
            return (new String(outStream.toByteArray(), "UTF-8"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("info", "File not found.");

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e("info", "File not found.");
        }
        return null;

    }

    /**
     * write string to file
     *
     * @param data
     */
    private void writeInputStreamToCache(String data) {
        try {
            if (!(Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED))) {
                FileOutputStream stream = mContext.openFileOutput(mFileName,
                        Context.MODE_PRIVATE);

                byte[] buf = data.getBytes();
                stream.write(buf);
                stream.close();
                return;
            }

            String cacheDir = this.getCacheDir();
            File saveFile = new File(cacheDir, mFileName);

            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(data.getBytes());
            outStream.close();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("info", "File not found.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("info", "File write error.");
        }
    }

    public void deleteGlobalFile() {
        String cacheDir = getCacheDir();
        File saveFile = new File(cacheDir, mFileName);
        if (saveFile.exists()) {
            saveFile.delete();
            Log.d("GlobalFile:", "deleteGlobalFile");
        }
        mCachesMap = new HashMap<String, Object>();
    }

    private String getCacheDir() {
        File file = GlobalConstants.FILE_FILE_DIR;
        String path = GlobalConstants.FILE_FILE_DIR.getAbsolutePath();
        return GlobalConstants.FILE_FILE_DIR.getAbsolutePath();
    }

    public static void Login(Context context,String status){
        Intent intent = new Intent(GlobalConstants.LOGIN_ACTION);
        intent.putExtra("status",status);
        context.sendBroadcast(intent);
    }

}

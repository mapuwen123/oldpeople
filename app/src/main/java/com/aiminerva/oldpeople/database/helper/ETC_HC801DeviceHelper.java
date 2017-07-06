package com.aiminerva.oldpeople.database.helper;

import android.content.ContentValues;
import android.database.Cursor;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.bean.BloodOxgenInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.database.contentprovider.ETC_HC801Constants;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC801B;

import java.util.ArrayList;

public class ETC_HC801DeviceHelper {
    static String TAG = "ETC_HC801DeviceHelper";

    private ETC_HC801DatabaseHelper mEtc_HC801DatabaseHelper;

    private static ETC_HC801DeviceHelper gETC_HC801DeviceHelper;

    synchronized public static ETC_HC801DeviceHelper getInstance() {
        if (gETC_HC801DeviceHelper == null) {
            gETC_HC801DeviceHelper = new ETC_HC801DeviceHelper();
        }
        return gETC_HC801DeviceHelper;
    }

    private ETC_HC801DeviceHelper() {
        mEtc_HC801DatabaseHelper = new ETC_HC801DatabaseHelper(
                MyApplication.getContext());
    }

    public boolean storeDeviceData(String userId, long oxgenValue,
                                   long pulseRate) {

        try {
            ContentValues values = new ContentValues();
            values.put(ETC_HC801Constants.DATE, System.currentTimeMillis());
            values.put(ETC_HC801Constants.UDID, PrtEtc_HC801B.UDID);
            values.put(ETC_HC801Constants.USERID, userId);
            values.put(ETC_HC801Constants.OXGEN_VALUE, oxgenValue);
            values.put(ETC_HC801Constants.PLUSRATE_VALUE, pulseRate);
            values.put(ETC_HC801Constants.VALUETYPE, 4);
            values.put(ETC_HC801Constants.HASSENT, 0);
            values.put(ETC_HC801Constants.MSGID,
                    "msgId_" + System.currentTimeMillis());

            mEtc_HC801DatabaseHelper.insert(values);

        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }

        return true;
    }

    public ArrayList<HealthIndicesInfo> getHasNoSentDatas(String userId) {
        Cursor cursor = mEtc_HC801DatabaseHelper.query(userId, false);

        if (cursor == null) {
            return null;
        }

        ArrayList<HealthIndicesInfo> healths = new ArrayList<HealthIndicesInfo>();
        if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            // mEtc_HC801DatabaseHelper.close();

            return healths;
        }

        do {
            // HealthIndicesInfo health = new HealthIndicesInfo();
            BloodOxgenInfo health = new BloodOxgenInfo();

            health.mDate = cursor.getLong(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.DATE));
            health.mUDID = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.UDID));
            health.mUserId = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.USERID));
            health.mValueType = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.VALUETYPE));
            health.mHasSent = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.HASSENT));
            health.mOxgenValue = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.OXGEN_VALUE));
            health.mPlusState = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.PLUSRATE_VALUE));
            health.mMsgId = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.MSGID));
            healths.add(health);

        } while (cursor.moveToNext());

        return healths;
    }

    public boolean removeDeviceData(HealthIndicesInfo info) {

        mEtc_HC801DatabaseHelper.delete(info.mUserId, info.mMsgId);
        return false;

    }

    public boolean removeDeviceAllData() {
        mEtc_HC801DatabaseHelper.delete();
        return false;

    }

    public BloodOxgenInfo getNewData(String userId) {
        Cursor cursor = mEtc_HC801DatabaseHelper.query(userId);

        if (cursor == null) {
            return null;
        }
        ArrayList<HealthIndicesInfo> healths = new ArrayList<HealthIndicesInfo>();
        if (cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            // mEtc_HC801DatabaseHelper.close();

            return null;
        }

        do {
            BloodOxgenInfo health = new BloodOxgenInfo();

            health.mDate = cursor.getLong(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.DATE));
            health.mUDID = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.UDID));
            health.mUserId = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.USERID));
            health.mValueType = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.VALUETYPE));
            health.mHasSent = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.HASSENT));
            health.mOxgenValue = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.OXGEN_VALUE));
            health.mPlusState = cursor.getInt(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.PLUSRATE_VALUE));
            health.mMsgId = cursor.getString(cursor
                    .getColumnIndexOrThrow(ETC_HC801Constants.MSGID));
            healths.add(health);

        } while (cursor.moveToNext());

        if (healths.size() > 0) {
            return (BloodOxgenInfo) healths.get(healths.size() - 1);
        }
        return null;
    }

    public ArrayList<HealthIndicesInfo> getAllHasNoSentDatas() {
        return null;
    }
}

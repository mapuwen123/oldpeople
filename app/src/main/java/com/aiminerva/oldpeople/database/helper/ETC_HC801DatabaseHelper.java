package com.aiminerva.oldpeople.database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aiminerva.oldpeople.database.contentprovider.ETC_HC801Constants;
import com.aiminerva.oldpeople.database.contentprovider.ETC_HC801ContentProvider;

public class ETC_HC801DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ETC_HC801DatabaseHelper";

    private static final String DATABASE_NAME = "bluetoothDeviceData.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL = "CREATE TABLE "
            + ETC_HC801ContentProvider.TABLE_NAME + " ("
            + ETC_HC801Constants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ETC_HC801Constants.DATE + " INTEGER(15),"
            + ETC_HC801Constants.UDID + " TEXT," + ETC_HC801Constants.USERID
            + " TEXT," + ETC_HC801Constants.VALUETYPE + " INTEGER,"
            + ETC_HC801Constants.HASSENT + " INTEGER,"
            + ETC_HC801Constants.OXGEN_VALUE + " INTEGER,"
            + ETC_HC801Constants.PLUSRATE_VALUE + " INTEGER ,"
            + ETC_HC801Constants.MSGID + " TEXT);";

    public ETC_HC801DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "CREATEGROUPCHATSSQL : " + SQL);
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + ETC_HC801ContentProvider.TABLE_NAME);
        onCreate(db);
    }

    public long insert(ContentValues initialValues) {

        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(ETC_HC801ContentProvider.TABLE_NAME, null,
                initialValues);

        if (rowId < 0) {
            throw new IllegalArgumentException("Cannot insert into table "
                    + ETC_HC801ContentProvider.TABLE_NAME);
        }
        return rowId;
    }

    public int delete(String userId, String msgId) {
        SQLiteDatabase db = getWritableDatabase();

        int count = db.delete(ETC_HC801ContentProvider.TABLE_NAME,
                ETC_HC801Constants.USERID + "='" + userId + "' and "
                        + ETC_HC801Constants.MSGID + "='" + msgId + "'", null);

        if (count < 0) {
            throw new IllegalArgumentException("Cannot delete item from table "
                    + ETC_HC801ContentProvider.TABLE_NAME);
        }
        return count;
    }

    public int delete(String userId) {
        SQLiteDatabase db = getWritableDatabase();

        int count = db.delete(ETC_HC801ContentProvider.TABLE_NAME,
                ETC_HC801Constants.USERID + "='" + userId + "'", null);

        if (count < 0) {
            throw new IllegalArgumentException("Cannot delete item from table "
                    + ETC_HC801ContentProvider.TABLE_NAME);
        }
        return count;
    }

    public int delete() {
        SQLiteDatabase db = getWritableDatabase();

        int count = db.delete(ETC_HC801ContentProvider.TABLE_NAME,
                "", null);

        if (count < 0) {
            throw new IllegalArgumentException("Cannot delete item from table "
                    + ETC_HC801ContentProvider.TABLE_NAME);
        }
        return count;
    }

    public Cursor query(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ETC_HC801ContentProvider.TABLE_NAME, null,
                ETC_HC801Constants.USERID + "=" + userId, null, null, null,
                null);
        return cursor;
    }

    public Cursor query(String userId, boolean hasSent) {

        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            String sql = ETC_HC801Constants.USERID + "= '" + userId + "' and' "
                    + ETC_HC801Constants.HASSENT + "'=" + hasSent;

            cursor = db.query(ETC_HC801ContentProvider.TABLE_NAME, null,
                    ETC_HC801Constants.USERID + "= '" + userId + "'AND "
                            + ETC_HC801Constants.HASSENT + "="
                            + (hasSent ? 1 : 0), null, null, null, null);


        } catch (IllegalArgumentException exception) {
            throw exception;

        }

        return cursor;
    }

    public int update(String userId, String msgId, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        int count = db.update(ETC_HC801ContentProvider.TABLE_NAME, values,
                ETC_HC801Constants.USERID + "='" + userId + "' and "
                        + ETC_HC801Constants.MSGID + "='" + msgId + "'", null);
        return count;
    }

}
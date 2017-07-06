package com.aiminerva.oldpeople.database.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.aiminerva.oldpeople.database.helper.ETC_HC801DatabaseHelper;
import com.orhanobut.logger.Logger;

public class ETC_HC801ContentProvider extends ContentProvider {

    private static final String TAG = "ETC_HC801ContentProvider";
    public static final String AUTHORITY = "com.andlisoft.BluetoothChat.database.devicedata��etc_hc801";
    public static final String TABLE_NAME = "etc_hc801";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private SQLiteOpenHelper mOpenHelper;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

//	static {
//		URI_MATCHER.addURI(AUTHORITY, TABLE_NAME, MESSAGES);
//		URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/#", MESSAGE_ID);
//	}

    public ETC_HC801ContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ETC_HC801DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(TABLE_NAME, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
//		if (URI_MATCHER.match(uri) != MESSAGES) {
//			throw new IllegalArgumentException("Cannot insert into URL: " + uri);
//		}
//
//		ContentValues values = (initialValues != null) ? new ContentValues(
//				initialValues) : new ContentValues();
//
//		for (String colName : ETC_HC801Constants.getRequiredColumns()) {
//			if (values.containsKey(colName) == false) {
//				throw new IllegalArgumentException("Missing column: " + colName);
//			}
//		}
//
//		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//		long rowId = db.insert(TABLE_NAME, ETC_HC801Constants.DATE, values);
//		if (rowId < 0) {
//			throw new SQLException("Failed to insert row into " + uri);
//		}
        Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, 1);
        getContext().getContentResolver().notifyChange(noteUri, null);
        return noteUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qBuilder.query(db, projection, selection, selectionArgs,
                null, null, null);

        if (ret == null) {
            Logger.d("ChatProvider.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        long rowId = 0;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();


        getContext().getContentResolver().notifyChange(uri, null);
        return 1;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}

package com.aiminerva.oldpeople.database.contentprovider;

import android.provider.BaseColumns;

import java.util.ArrayList;

public class ETC_HC801Constants implements BaseColumns {

    private ETC_HC801Constants() {
    }

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.andlisoft.chats";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.andlisoft.chats";
    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    public static final String DATE = "date";// 鏃堕棿
    public static final String UDID = "udid"; //璁惧id
    public static final String USERID = "userid";   // 閲囬泦鐢ㄦ埛
    public static final String VALUETYPE = "valuetype";    //璁惧绫诲瀷
    public static final String HASSENT = "hassent";// 鏄惁鍙戦�
    public static final String OXGEN_VALUE = "oxgen_value";// 閲囬泦鍊�
    public static final String PLUSRATE_VALUE = "plusrate_value";// 閲囬泦鍊�
    public static final String MSGID = "msgid";// 消息标识


    public static final String[] PROJECTION_FROM = new String[]{_ID,
            DATE, UDID, USERID, VALUETYPE, HASSENT, OXGEN_VALUE, PLUSRATE_VALUE, MSGID};// 鏌ヨ瀛楁

    public static ArrayList<String> getRequiredColumns() {
        ArrayList<String> tmpList = new ArrayList<String>();
        tmpList.add(DATE);
        tmpList.add(UDID);
        tmpList.add(USERID);
        tmpList.add(VALUETYPE);
        tmpList.add(HASSENT);
        tmpList.add(OXGEN_VALUE);
        tmpList.add(PLUSRATE_VALUE);
        tmpList.add(MSGID);

        return tmpList;
    }
}

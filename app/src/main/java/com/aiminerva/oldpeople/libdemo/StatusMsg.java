package com.aiminerva.oldpeople.libdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 2017/2/16.
 */

public class StatusMsg
{
    public static final int MEASUREATAGE_DETECTING = 0;
    public static final int MEASUREATAGE_PREPARE = 1;
    public static final int MEASUREATAGE_MEASURING = 2;
    public static final int MEASUREATAGE_OVER = 3;
    public static final int SMOOTHMODE_NORMAL = 0;
    public static final int SMOOTHMODE_ENHANCE = 128;
    public static final int TRANSMIT_MODE_CONTINUOUS = 0;
    public static final int TRANSMIT_MODE_FILE = 1;
    public static final int TRANSMIT_MODE_QUICK = 2;
    public static final int ECG_RESPONSE_ACK = 0;
    public static final int ECG_RESPONSE_REJ = 1;
    public static String rDeviceName = "";

    public static List<Integer> LeakPack = new ArrayList();

    public static int SENDMODE = 3;
    public static final int FILE_TRANSMIT_START = 0;
    public static final int FILE_TRANSMIT_SUCCESS = 1;
    public static final int FILE_TRANSMIT_ERROR = 3;
    public static final int ECG_RESULT_00 = 0;
    public static final int ECG_RESULT_01 = 1;
    public static final int ECG_RESULT_02 = 2;
    public static final int ECG_RESULT_03 = 3;
    public static final int ECG_RESULT_04 = 4;
    public static final int ECG_RESULT_05 = 5;
    public static final int ECG_RESULT_06 = 6;
    public static final int ECG_RESULT_07 = 7;
    public static final int ECG_RESULT_08 = 8;
    public static final int ECG_RESULT_09 = 9;
    public static final int ECG_RESULT_0a = 10;
    public static final int ECG_RESULT_0b = 11;
    public static final int ECG_RESULT_0c = 12;
    public static final int ECG_RESULT_0d = 13;
    public static final int ECG_RESULT_0e = 14;
    public static final int ECG_RESULT_0f = 15;
    public static final int ECG_RESULT_ff = 16;

    public static void cleanPRAR()
    {
        if (LeakPack != null)
            LeakPack.clear();
    }

    public static void init()
    {
        cleanPRAR();
    }
}
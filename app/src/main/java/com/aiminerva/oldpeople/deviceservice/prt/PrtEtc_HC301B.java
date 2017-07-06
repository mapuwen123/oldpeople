package com.aiminerva.oldpeople.deviceservice.prt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//体质解析
public class PrtEtc_HC301B extends PrtBase {

    public final static String UDID = "FAT:HC-301B";
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtEtc_HC301B";

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);
    private Context mContext = MyApplication.getContext();

    public interface PrtEtc_HC301BListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtBodyfatModel result);
    }

    public class PrtBodyfatModel extends PrtData {
        public int mHeight;// 身高
        public int mWeight;// 体重
        public int mYear;// 年龄
        public int mSex;// 性别
        public long mTime;// 测量时间
        public float mFatPercent;
        public float mBMIValue;
        public int mBasalMetabolism;// 基础代谢值
        public int mBMILevel;// 体质水平
        public int mHabitusLevel;// 体型水平
    }

    public PrtEtc_HC301B() {
        super(UDID);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        super.init();
        mRecvBuffer.clear();
    }

    @Override
    public void uinit() {
        // TODO Auto-generated method stub
        super.uinit();
        mRecvBuffer.clear();
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public synchronized boolean masterRec(byte[] buffer) {

        if (buffer == null) {
            return false;
        }

        mRecvBuffer.put(buffer);

        mRecvBuffer.flip();
        Log.e("limit", "limit " + mRecvBuffer.limit() + "position "
                + mRecvBuffer.position());

        while (0 < (mRecvBuffer.limit() - mRecvBuffer.position())) {

            byte data = (byte) (mRecvBuffer.get(mRecvBuffer.position()) & 0xFF);// 不改边postion位置
            byte data1 = 0;
            byte data2 = 0;

            int length = mRecvBuffer.limit() - mRecvBuffer.position();//接收长度

            if (length < 3) {
                byte[] remainBuffer = new byte[length];

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();
                mRecvBuffer.put(remainBuffer);

                return false;
            } else if (data == (byte) 0x53) {
                data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置

                if (data1 != (byte) 0x4E) {
                    mRecvBuffer.get();// position++
                    continue;
                }

                int packetLength = 3 + (int) (mRecvBuffer.get(mRecvBuffer.position() + 2) & 0xFF);

                if (length < packetLength) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                //parse packet
                PrtBodyfatModel model = new PrtBodyfatModel();
                model.mHeight = (mRecvBuffer.get(mRecvBuffer.position() + 4) & 0x00ff);
                model.mWeight = ((mRecvBuffer.get(mRecvBuffer.position() + 5) & 0x00ff)
                        + (mRecvBuffer.get(mRecvBuffer.position() + 6) & 0x00ff) * 256) / 10;
                model.mYear = (mRecvBuffer.get(mRecvBuffer.position() + 7) & 0x00ff);
                model.mSex = (mRecvBuffer.get(mRecvBuffer.position() + 8) & 0x00ff);

                int year = (mRecvBuffer.get(mRecvBuffer.position() + 9) & 0x00ff);
                int month = (mRecvBuffer.get(mRecvBuffer.position() + 10) & 0x00ff);
                int day = (mRecvBuffer.get(mRecvBuffer.position() + 11) & 0x00ff);
                int hour = (mRecvBuffer.get(mRecvBuffer.position() + 12) & 0x00ff);
                int minute = (mRecvBuffer.get(mRecvBuffer.position() + 13) & 0x00ff);

                Locale defloc = Locale.getDefault();
                SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm", defloc);// 10012710.17
                String sTime = String.format(defloc, "%2d%02d%02d%02d%2d", year, month,
                        day, hour, minute);
//				SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm");// 10012710.17
//				String sTime = String.format("%2d%02d%02d%02d%2d", year, month,
//						day, hour, minute);
                try {
                    Date date = format.parse(sTime);
                    model.mTime = date.getTime();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                model.mFatPercent = ((mRecvBuffer
                        .get(mRecvBuffer.position() + 14) & 0x00ff) + ((mRecvBuffer
                        .get(mRecvBuffer.position() + 15) & 0x00ff) << 8)) / 1000.f;
                model.mBMIValue = ((mRecvBuffer
                        .get(mRecvBuffer.position() + 16) & 0x00ff) + ((mRecvBuffer
                        .get(mRecvBuffer.position() + 17) & 0x00ff) << 8)) / 10.f;
                model.mBasalMetabolism = (mRecvBuffer.get(mRecvBuffer
                        .position() + 18) & 0x00ff)
                        + ((mRecvBuffer.get(mRecvBuffer.position() + 19) & 0x00ff) << 8);
                model.mBMILevel = (mRecvBuffer.get(mRecvBuffer.position() + 20) & 0x00ff);
                model.mHabitusLevel = (mRecvBuffer
                        .get(mRecvBuffer.position() + 21) & 0x00ff);

                if (mListener != null) {

                    PrtEtc_HC301BListener listener = (PrtEtc_HC301BListener) mListener;
                    listener.onMasterReceive(CMD_BODYFATVALUE, model);
                }
                break;

            } else {
                mRecvBuffer.get();// position++
                continue;
            }
        }
        mRecvBuffer.clear();
        return true;
    }

    public synchronized boolean masterSend(byte[] buffer) {
        if (mListener != null) {
            return mListener.onMasterSend(buffer);
        }
        return false;
    }

}

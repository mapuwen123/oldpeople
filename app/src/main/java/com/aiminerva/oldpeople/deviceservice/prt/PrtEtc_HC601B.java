package com.aiminerva.oldpeople.deviceservice.prt;

import android.annotation.SuppressLint;
import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//血糖协议解析
public class PrtEtc_HC601B extends PrtBase {

    public String TAG = "PrtEtc_HC601B";
    public final static String UDID = "BG:HC-601B";
    public final static String PASSWORD = "1234";

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);
    ;


    public interface PrtEtc_HC601BListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtBloodSugerModel result);
    }

    public class PrtBloodSugerModel extends PrtData {
        public int mTime;
        public float mBloodSuger;
    }

    public PrtEtc_HC601B() {
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

    @SuppressLint({"DefaultLocale", "SimpleDateFormat"})
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
            byte data1 = 0x00;
            int length = mRecvBuffer.limit() - mRecvBuffer.position();

            if (length < 2) {
                byte[] remainBuffer = new byte[length];

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();
                mRecvBuffer.put(remainBuffer);

                return false;
            } else if (data == (byte) 0x52) {
                data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置

                if (data1 != (byte) 0x42) {
                    mRecvBuffer.get();// position++
                    continue;
                }

                if (length < 12) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                byte[] pck = new byte[12];
                mRecvBuffer.get(pck, mRecvBuffer.position(), 12);

                PrtBloodSugerModel model = new PrtBloodSugerModel();

                int year = pck[2] & 0x00ff;
                int month = pck[3] & 0x00ff;
                int day = pck[4] & 0x00ff;
                int hour = pck[5] & 0x00ff;
                int minute = pck[6] & 0x00ff;

                Locale defloc = Locale.getDefault();
                SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm", defloc);// 10012710.17
                String sTime = String.format(defloc, "%2d%02d%02d%02d%2d", year, month,
                        day, hour, minute);
//				SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmm");// 10012710.17
//				String sTime = String.format("%2d%02d%02d%02d%2d", year, month,
//						day, hour, minute);
                try {
                    Date date = format.parse(sTime);
                    model.mTime = (int) date.getTime();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                float value = ((pck[7] & 0x00ff) * 256 + (pck[8] & 0x00ff)) / 18.0f;
                model.mBloodSuger = (float) ((long) (Math.round(value * 10)) / 10.0);//四舍五入 保留一位小数
                if (mListener != null) {

                    PrtEtc_HC601BListener listener = (PrtEtc_HC601BListener) mListener;
                    listener.onMasterReceive(CMD_BLOODSUGERVALUE, model);
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

    public synchronized boolean masterSnd(byte[] buf) {
        if (mListener != null) {
            return mListener.onMasterSend(buf);
        }
        return false;
    }
}

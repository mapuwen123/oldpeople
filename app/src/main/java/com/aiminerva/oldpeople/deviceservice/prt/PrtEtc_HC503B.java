package com.aiminerva.oldpeople.deviceservice.prt;

import android.content.Context;
import android.util.Log;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;

//血压计协议解析
public class PrtEtc_HC503B extends PrtBase {

    public final static String UDID = "BP:HC-503B";
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtEtc_HC503B";
    private final static int PACKET_LENGHT = 5;

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);
    private Context mContext = MyApplication.getContext();

    public interface PrtEtc_HC503BListener extends PrtBase.PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtBloodPressModel result);
    }

    public class PrtBloodPressModel extends PrtData {
        public int mSystolicPressure;//收缩压
        public int mDiastolicPressure;//舒张压
        public int mPluse;//脉搏数/分
    }

    public PrtEtc_HC503B() {
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
            byte data2 = 0x00;
            byte data3 = 0x00;
            int length = mRecvBuffer.limit() - mRecvBuffer.position();

            if (length < 4) {
                byte[] remainBuffer = new byte[length];

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();
                mRecvBuffer.put(remainBuffer);

                return false;
            } else if (data == (byte) 0x41) {
                data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置
                data2 = mRecvBuffer.get(mRecvBuffer.position() + 2);
                data3 = mRecvBuffer.get(mRecvBuffer.position() + 3);

                byte header1 = ascii2byte(data, data1);
                byte header2 = ascii2byte(data2, data3);

                if (header1 != (byte) 0xA0 || header2 != (byte) 0x00) {
                    mRecvBuffer.get();// position++
                    continue;
                }

                if (length < 32) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                PrtBloodPressModel model = new PrtBloodPressModel();
                byte HicSysPress0 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 4)) & 0x00ff);
                byte HicSysPress1 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 5)) & 0x00ff);
                byte HiSysPress = ascii2byte(HicSysPress0, HicSysPress1);

                byte LocSysPress0 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 6)) & 0x00ff);
                byte LocSysPress1 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 7)) & 0x00ff);
                byte LoSysPress = ascii2byte(LocSysPress0, LocSysPress1);

                model.mSystolicPressure = (int) (((short) HiSysPress) << 8) + (int) (LoSysPress & 0x00ff);

                byte cDiasPress0 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 8)) & 0x00ff);
                byte cDiasPress1 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 9)) & 0x00ff);
                byte DiasPress = ascii2byte(cDiasPress0, cDiasPress1);

                model.mDiastolicPressure = (DiasPress & 0x00ff);

                byte cPluse0 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 10)) & 0x00ff);
                byte cPluse1 = (byte) ((byte) (mRecvBuffer.get(mRecvBuffer.position() + 11)) & 0x00ff);
                byte Pluse = ascii2byte(cPluse0, cPluse1);

                model.mPluse = (Pluse & 0x00ff);

                if (mListener != null) {

                    PrtEtc_HC503BListener listener = (PrtEtc_HC503BListener) mListener;
                    listener.onMasterReceive(CMD_BLOODPRESSVALUE, model);
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

    static byte ascii2byte(byte hichar, byte lochar) {
        if (hichar < 48 || hichar > 70 || lochar < 48 || lochar > 70) {
            return 0;
        }

        return (byte) (Integer.valueOf(String.format("%c%c", hichar, lochar), 16) & 0x000000ff);
    }


    public synchronized boolean masterSend(byte[] buffer) {
        if (mListener != null) {
            return mListener.onMasterSend(buffer);
        }
        return false;
    }
}

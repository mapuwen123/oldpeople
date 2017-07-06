package com.aiminerva.oldpeople.deviceservice.prt;

import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;

public class PrtMdl_BT20_203B extends PrtBase {

    public final static String UDID = "MDL-";//设备UDID
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtMDL_BT20_203B";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    private final static byte PDU_START_1 = (byte) 0xFF;
    private final static byte PDU_START_2 = (byte) 0xFA;
    private final static byte PDU_RU_ID = 0x20;
    private final static byte PDU_UR_ID = 0x01;

    private final static byte ASDU_UR_CNN_OK = (byte) 0xA7;
    private final static byte ASDU_UR_PREMEASURE = (byte) 0xA0;
    private final static byte ASDU_UR_STOPMEASURE = (byte) 0xA3;
    private final static byte ASDU_UR_VERSION = (byte) 0xA4;

    private final static byte ASDU_RU_CNN = 0x51;
    private final static byte ASDU_RU_PREMEASURE = 0x52;
    private final static byte ASDU_RU_STARTMEASURE = 0x50;
    private final static byte ASDU_RU_STOPMEASURE_OK = 0x53; //0字节
    private final static byte ASDU_RU_REALRESULT = 0x55; //
    private final static byte ASDU_RU_ERRRESULT = 0x56;//
    private final static byte ASDU_RU_BATTERY = (byte) 0xDD;
    private final static byte ASDU_RU_VESION = 0x58;

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] mbSendBuffer;
    public byte[] mbRecBuffer;

    public interface PrtMdl_BT20_203BListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtMDLBloodPressModel result);
    }

    public class PrtMDLBloodPressModel extends PrtData {
        public int mSystolicPressure;//收缩压
        public int mDiastolicPressure;//舒张压
        public int mPluse;//脉搏数/分
    }

    public PrtMdl_BT20_203B() {
        super("");
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

    public boolean sendConnectOk() {
        byte buff[] = onPackAsdu_UR_CNN_OK();
        return masterSnd(buff);
    }

    public boolean sendPreMeasure() {
        byte buff[] = onPackAsdu_UR_PREMEASURE();
        return masterSnd(buff);
    }

    public boolean sendStopMeasure() {
        byte buff[] = onPackAsdu_UR_STOPMEASURE();
        return masterSnd(buff);
    }

    private byte[] onPackAsdu_UR_CNN_OK() {
        mbSendBuffer = new byte[6];
        mbSendBuffer[0] = PDU_START_1;
        mbSendBuffer[1] = PDU_START_1;
        mbSendBuffer[2] = 0x04;
        mbSendBuffer[3] = (byte) 0xAC;
        mbSendBuffer[4] = 0x01;
        mbSendBuffer[5] = (byte) 0xA7;
        return mbSendBuffer;
    }

    private byte[] onPackAsdu_UR_PREMEASURE() {
        mbSendBuffer = new byte[6];
        mbSendBuffer[0] = PDU_START_1;
        mbSendBuffer[1] = PDU_START_1;
        mbSendBuffer[2] = 0x04;
        mbSendBuffer[3] = (byte) 0XA5;
        mbSendBuffer[4] = 0x01;
        mbSendBuffer[5] = (byte) 0xA0;
        return mbSendBuffer;
    }

    private byte[] onPackAsdu_UR_STOPMEASURE() {
        mbSendBuffer = new byte[6];
        mbSendBuffer[0] = PDU_START_1;
        mbSendBuffer[1] = PDU_START_1;
        mbSendBuffer[2] = 0x04;
        mbSendBuffer[3] = (byte) 0xA8;
        mbSendBuffer[4] = 0x01;
        mbSendBuffer[5] = (byte) 0xA3;
        return mbSendBuffer;
    }

    //读取血压计信息
    public boolean sendReadVersion() {
        byte buff[] = onPackAsdu_UR_VERSION();
        return masterSnd(buff);
    }

    private byte[] onPackAsdu_UR_VERSION() {
        mbSendBuffer = new byte[6];
        mbSendBuffer[0] = PDU_START_1;
        mbSendBuffer[1] = PDU_START_1;
        mbSendBuffer[2] = 0x04;
        mbSendBuffer[3] = (byte) 0xA9;
        mbSendBuffer[4] = 0x01;
        mbSendBuffer[5] = (byte) 0xA4;
        return mbSendBuffer;
    }

    private PrtMDLBloodPressModel onUnPackRealResult(byte[] packet) {
        PrtMDLBloodPressModel model = new PrtMDLBloodPressModel();
        model.mSystolicPressure = (short) packet[6] * 256 + (short) packet[7];
        model.mDiastolicPressure = (short) packet[8] * 256 + (short) packet[9];
        model.mPluse = (short) packet[10];
        return model;
    }

    public synchronized boolean masterRec(byte[] buffer) {

        if (buffer == null) {
            return false;
        }

        //-------------------
        mRecvBuffer.put(buffer);

        mRecvBuffer.flip();

        while (0 < (mRecvBuffer.limit() - mRecvBuffer.position())) {

            byte data = mRecvBuffer.get(mRecvBuffer.position());//不改边postion位置
            int length = mRecvBuffer.limit() - mRecvBuffer.position() + 1;

            Log.e(TAG, "RecvBuffer first data " + data);
            Log.e(TAG, "RecvBuffer  length" + length);

            if (length < 5) {

                byte[] remainBuffer = new byte[length];
                Log.e(TAG, "RecvBuffer  transfering ...");

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();

                mRecvBuffer.put(remainBuffer);

                Log.e(TAG, "RecvBuffer  transfered");

                return false;

            } else if ((data) == PDU_START_1) {
                byte data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置

                if (data1 != (byte) PDU_START_2) {
                    mRecvBuffer.get();// position++
                    continue;
                }

                int packetLength = 2 + (int) (mRecvBuffer.get(mRecvBuffer.position() + 2));

                if (length < packetLength) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                byte[] pck = new byte[packetLength];
                mRecvBuffer.get(pck, mRecvBuffer.position(), packetLength);

                int pduCode = (int) (pck[4]);
                switch ((int) (pck[4])) {
                    case ASDU_RU_CNN:
                        sendConnectOk();
                    case ASDU_RU_PREMEASURE:
                        sendPreMeasure();//ASDU_UR_PREMEASURE
                    case ASDU_RU_STARTMEASURE:
                        //确认开始
                    case ASDU_RU_STOPMEASURE_OK:
                        //血压计确认测量中止
                    case ASDU_RU_REALRESULT:
                        onUnPackRealResult(pck);//解析实时数据
                    case ASDU_RU_BATTERY:
                        //onUnPackBattery();//电压低
                    case ASDU_RU_VESION:
                        //读取血压计版本信息
                        sendReadVersion();
                }

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

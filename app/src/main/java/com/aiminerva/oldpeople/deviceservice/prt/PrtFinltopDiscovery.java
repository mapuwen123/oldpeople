package com.aiminerva.oldpeople.deviceservice.prt;

import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;

public class PrtFinltopDiscovery extends PrtBase {

    public final static String UDID = "finltop";//设备UDID
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtFinltopDiscovery";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    private final static byte PDU_RU_ID = (byte) 0xff; //手机
    private final static byte PDU_UR_ID = 0x03; //测量单元

//	private final static byte ASDU_PACK_CNN_OK = (byte)0xfe;
//	private final static byte ASDU_PACK_TIME_SYNC = (byte)0x00;
//	private final static byte ASDU_PACK_TRANSMIT = (byte)0x03;
//	private final static byte ASDU_PACK_VERSION = (byte)0xfb;
//	private final static byte ASDU_PACK_ERR = (byte)0xff;

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] mbSendBuffer;
    public byte[] mbRecBuffer;

    //    private boolean bTransmitOk = false;
    private byte[] mID = new byte[4];
    private byte mType;//设备类型

    private PrtFT_FinltopDevModel mModel;

    public interface PrtFt_FinltopDiscoveryListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtFT_FinltopDevModel result);
    }

    public class PrtFT_FinltopDevModel extends PrtData {
        public String mUDID;
        public int mType;
    }

    public PrtFinltopDiscovery() {
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

    public byte checkSum(byte[] buffer) {
        byte check = 0;
        for (int i = 0; i < buffer.length - 1; i++) {
            check ^= buffer[i];
        }
        return (byte) (check & 0x00FF);
    }

    public boolean sendSsdp() {
        byte buff[] = onPackAsdu_UR_SEARCH_OK();
        return masterSnd(buff);
    }

    byte[] onPackAsdu_UR_SEARCH_OK() {
        mbSendBuffer = new byte[8];
        mbSendBuffer[0] = 0x4f;
        mbSendBuffer[1] = PDU_RU_ID;
        mbSendBuffer[2] = (byte) 0xff;
        mbSendBuffer[3] = (byte) 0xff;
        mbSendBuffer[4] = 0x02;
        mbSendBuffer[5] = (byte) 0xff;
        mbSendBuffer[6] = (byte) 0xff;
        mbSendBuffer[7] = checkSum(mbSendBuffer);
        return mbSendBuffer;
    }

    private boolean onUnPackDeviceID(byte[] pack) {
        if (pack.length != 8)
            return false;

        mType = pack[1];
        mID[0] = pack[2];
        mID[1] = pack[3];
        mID[2] = pack[5];
        mID[3] = pack[6];
        return true;
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

                mRecvBuffer.get(remainBuffer, mRecvBuffer.limit(), length);
                mRecvBuffer.clear();

                mRecvBuffer.put(remainBuffer);

                Log.e(TAG, "RecvBuffer  transfered");

                return false;

            } else if ((data & 0xF0) == 0x40 || (data & 0xF0) == 0x50) {
                byte data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置

                if (data1 != (byte) 0x02 || data1 != (byte) 0x03) {//只支持两个设备
                    mRecvBuffer.get();// position++
                    continue;
                }

                //TODO
                int packetLength = 6 + (int) (mRecvBuffer.get(mRecvBuffer.position() + 4));

                if (length < packetLength) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                byte[] pck = new byte[packetLength];
                mRecvBuffer.get(pck, mRecvBuffer.position(), packetLength);

                if (pck.length == 8) {
                    onUnPackDeviceID(pck);
                    //探针成功
                    PrtFT_FinltopDevModel model = new PrtFT_FinltopDevModel();
                    model.mUDID = String.valueOf(mID[0]) + String.valueOf(mID[1]) +
                            String.valueOf(mID[2]) + String.valueOf(mID[3]);
                    model.mType = (int) mType;

                    if (mListener != null) {
                        PrtFt_FinltopDiscoveryListener listener = (PrtFt_FinltopDiscoveryListener) mListener;
                        listener.onMasterReceive(mType, mModel);
                    }
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

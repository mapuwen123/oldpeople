package com.aiminerva.oldpeople.deviceservice.prt;

import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;

public class PrtSino_WL1 extends PrtBase {

    public final static String UDID = "Sinocare";//设备UDID
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtSino_WL1";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    public final static byte COMMAND_TYPE_TEST = 0x01;
    public final static byte COMMAND_TYPE_ERR = 0x02;
    public final static byte COMMAND_TYPE_LIVEDATA = 0x04;
    public final static byte COMMAND_TYPE_START = 0x0A;//开始测量
    public final static byte COMMAND_TYPE_ShUTDOWN = 0x0b;

    //packete content
    public final static byte PACKET_HEADER_1 = 0x53;
    public final static byte PACKET_HEADER_2 = 0x4e;
    public final static byte MACHINE_CODE_00 = 0X00;
    public final static byte MACHINE_CODE_01 = 0X04;

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] mbSendBuffer;
    public byte[] mbRecBuffer;

    public interface PrtSino_WL1Listener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtSinoOxgenMode result);
    }

    public class PrtSinoOxgenMode extends PrtData {
        public int mTime;
        public float mBloodSuger;
        public int mStep;//操作步骤
    }

    public PrtSino_WL1() {
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

    public boolean sendTestConnect() {
        mbSendBuffer = new byte[11];
        mbSendBuffer[0] = PACKET_HEADER_1;
        mbSendBuffer[1] = PACKET_HEADER_2;
        mbSendBuffer[2] = 8;
        mbSendBuffer[3] = MACHINE_CODE_00;
        mbSendBuffer[4] = MACHINE_CODE_01;
        mbSendBuffer[5] = 0x01;
        mbSendBuffer[6] = 0x53;
        mbSendBuffer[7] = 0x49;
        mbSendBuffer[8] = 0x4e;
        mbSendBuffer[9] = 0x4f;
        mbSendBuffer[10] = checkSum(mbSendBuffer);
        return masterSnd(mbSendBuffer);

    }

    public byte checkSum(byte[] buffer) {
        short check = 0;
        for (int i = 2; i < buffer.length - 1; i++) {
            check += buffer[i];
        }
        return (byte) (check & 0x00FF);
    }

    public boolean onUnpack_Test(byte[] buffer) {
        if (buffer[0] != PACKET_HEADER_1 && buffer[1] != PACKET_HEADER_2) {
            return false;
        }

        if (buffer[2] != 8) {
            return false;
        }

        if (buffer[5] != COMMAND_TYPE_TEST) {
            return false;
        }

        if (buffer[10] != checkSum(buffer)) {
            return false;
        }
        return true;
    }

    public boolean onUnpack_Shutdown(byte[] buffer) {
        if (buffer[0] != PACKET_HEADER_1 && buffer[1] != PACKET_HEADER_2) {
            return false;
        }

        if (buffer[2] != 6) {
            return false;
        }

        if (buffer[5] != COMMAND_TYPE_ShUTDOWN) {
            return false;
        }

        if (buffer[8] != checkSum(buffer)) {
            return false;
        }
        return true;
    }

    public boolean sendQueryLiveData() {

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

            if (length < 3) {

                byte[] remainBuffer = new byte[length];
                Log.e(TAG, "RecvBuffer  transfering ...");

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();

                mRecvBuffer.put(remainBuffer);

                Log.e(TAG, "RecvBuffer  transfered");

                return false;

            } else if ((data) == PACKET_HEADER_1) {
                byte data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置

                if (data1 != (byte) PACKET_HEADER_2) {
                    mRecvBuffer.get();// position++
                    continue;
                }

                int packetLength = 3 + (int) (mRecvBuffer.get(mRecvBuffer.position() + 2));

                if (length < packetLength) {
                    byte[] remainBuffer = new byte[length];

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();
                    mRecvBuffer.put(remainBuffer);

                    return false;
                }

                byte[] pck = new byte[packetLength];
                mRecvBuffer.get(pck, mRecvBuffer.position(), packetLength);

//				int cmdcode = (int)(mRecvBuffer.get(mRecvBuffer.position() +5 ));
                int cmdcode = (int) (pck[5]);

                if (cmdcode == COMMAND_TYPE_LIVEDATA) {// 实时数据

                    PrtSinoOxgenMode model = new PrtSinoOxgenMode();
                    model.mTime = 0;
                    model.mBloodSuger = (float) (pck[11] * 256 + pck[12]) / 10.0f;
                    model.mStep = COMMAND_TYPE_LIVEDATA;

                    if (mListener != null) {

                        PrtSino_WL1Listener listener = (PrtSino_WL1Listener) mListener;
                        listener.onMasterReceive(CMD_BLOODSUGERVALUE, model);
                    }

                    break;
                } else if (cmdcode == COMMAND_TYPE_START) {//开始测量

                    PrtSinoOxgenMode model = new PrtSinoOxgenMode();
                    model.mStep = COMMAND_TYPE_START;

                    if (mListener != null) {

                        PrtSino_WL1Listener listener = (PrtSino_WL1Listener) mListener;
                        listener.onMasterReceive(CMD_BLOODSUGERVALUE, model);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                } else if (cmdcode == COMMAND_TYPE_ERR) {
                    PrtSinoOxgenMode model = new PrtSinoOxgenMode();
                    model.mStep = COMMAND_TYPE_ERR;

                    if (mListener != null) {

                        PrtSino_WL1Listener listener = (PrtSino_WL1Listener) mListener;
                        listener.onMasterReceive(CMD_BLOODSUGERVALUE, model);
                    }
                }
//				}else{
//					byte[] buf = new byte[packetLength];
//					mRecvBuffer.get(buf, mRecvBuffer.position(), packetLength);
//				}

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

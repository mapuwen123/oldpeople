package com.aiminerva.oldpeople.deviceservice.prt;

import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.PrtBase;

import java.nio.ByteBuffer;


/**
 * etc-comm 血糖仪蓝牙协议解析
 *
 * @author cgq
 */
public class PrtEtc_HC801B extends PrtBase {

    public String TAG = "PrtEtc_HC801B";
    public final static String UDID = "SPO2:HC-801B";
    public final static String PASSWORD = "1234";

    public final static byte SYNC_BIT_HI = (byte) 0x80;
    public final static byte SYNC_BIT_LO = 0x00;

    //	public final static byte CMD_BLOODVALUE = 0x01;
    public final static int PACKET_LENGHT = 5;
    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);
    ;


    public interface PrtEtc_HC801BListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtOxgenModel result);
    }

    public class PrtOxgenModel extends PrtData {
        public int pulseRate;
        public int oxgen;
    }


    public PrtEtc_HC801B() {
        super(UDID);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        super.init();
    }

    @Override
    public void uinit() {
        // TODO Auto-generated method stub
        super.uinit();
        mRecvBuffer.clear();
    }

    public synchronized boolean masterRec(byte[] buffer) {

        Log.e(TAG, "MasterRecv " + buffer.length);

        mRecvBuffer.put(buffer);

        Log.e(TAG, "RecvBuffer position " + mRecvBuffer.position());
        if (mRecvBuffer.position() < PACKET_LENGHT - 1) {
            return false;
        }

        Log.e(TAG, "start unpacket!");

        mRecvBuffer.flip();
        Log.e(TAG, "limit " + mRecvBuffer.limit() + "position " + mRecvBuffer.position());

        while (0 < (mRecvBuffer.limit() - mRecvBuffer.position())) {

            byte data = mRecvBuffer.get(mRecvBuffer.position());//不改边postion位置
            int length = mRecvBuffer.limit() - mRecvBuffer.position();

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

            } else if ((data & 0x80) == 0x00) {
                mRecvBuffer.get();//postion++
                continue;
            }

            byte[] prtPacket = new byte[5];
            mRecvBuffer.get(prtPacket, 0, 5);

//			Log.e("prtPacket", "prtpacket[0]" + prtPacket[0]);
//			Log.e("prtPacket", "prtpacket[1]" + prtPacket[1]);
//			Log.e("prtPacket", "prtpacket[2]" + prtPacket[2]);
//			Log.e("prtPacket", "prtpacket[3]" + prtPacket[3]);
//			Log.e("prtPacket", "prtpacket[4]" + prtPacket[4]);

            if (this.isPacketValid(prtPacket)) {
                PrtOxgenModel model = new PrtOxgenModel();
                boolean result = onUnpack_BloodValue(prtPacket, model);
                Log.e(TAG, "result = " + result);

                if (mListener != null) {
                    if (result == true) {
                        PrtEtc_HC801BListener listener = (PrtEtc_HC801BListener) mListener;
                        listener.onMasterReceive(CMD_BLOODOXYGENVALUE, model);
                    } else {
                        mListener.onMasterErr();
                    }
                }
            } else {
                String packet = "";
                for (byte b : prtPacket) {
                    packet += b;
                }
                Log.e("Prt", "receive prtPacket error:" + packet);
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

    private boolean isPacketValid(byte[] buf) {
        if (buf.length != 5) {
            return false;
        }
        byte a = (byte) ((buf[0]) & 0x80);
        // if (((buf[0]) & 0x80) != SYNC_BIT_HI) {
        if (a != SYNC_BIT_HI) {
            return false;
        }
        if ((buf[1] & 0x80) != SYNC_BIT_LO) {
            return false;
        }
        if ((buf[2] & 0x80) != SYNC_BIT_LO) {
            return false;
        }
        if ((buf[3] & 0x80) != SYNC_BIT_LO) {
            return false;
        }
        if ((buf[4] & 0x80) != SYNC_BIT_LO) {
            return false;
        }
        return true;
    }

    boolean onUnpack_BloodValue(byte[] packet, PrtOxgenModel resluts) {
        if (packet.length != 5) {
            return false;
        }
        resluts.pulseRate = packet[3];
        resluts.oxgen = packet[4];
        Log.e(TAG, "pulseRate = " + packet[3] + "oxgen" + packet[4] + "packet length" + packet.length + "packet[0]" + packet[0] + "packet[1]" + packet[1] + "packet[2]" + packet[2] + "packet[3]" + packet[3]);
        return true;
    }
}

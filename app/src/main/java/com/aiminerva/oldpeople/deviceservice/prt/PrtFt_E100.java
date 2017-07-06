package com.aiminerva.oldpeople.deviceservice.prt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.deviceservice.PrtBase;
import com.aiminerva.oldpeople.globalsettings.GlobalConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrtFt_E100 extends PrtBase {

    public final static byte ID = 0x02;
    public final static String UDID = "finltop";//设备UDID
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtFt_E100";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    private final static byte PDU_RU_ID = (byte) 0xff; //手机
    private final static byte PDU_UR_ID = 0x02; //测量单元

    private final static byte ASDU_PACK_CNN_OK = (byte) 0xfe;
    private final static byte ASDU_PACK_TIME_SYNC = (byte) 0x00;
    private final static byte ASDU_PACK_TRANSMIT = (byte) 0x02;//心电数据包
    private final static byte ASDU_PACK_VERSION = (byte) 0xfb;
    private final static byte ASDU_PACK_ERR = (byte) 0xff;

    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] mbSendBuffer;
    public byte[] mbRecBuffer;

    private boolean bTransmitOk = false;
    private byte[] mID = new byte[4];

    private PrtFtE100Model mModel;
    private DataModel mDataModel;
    private String mFileName = "";
    private Context mContext = MyApplication.getContext();

    public interface PrtFt_E100Listener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtFtE100Model result);
    }

    private class PackageModel {
        public int mLenNo;// 心电条数No
        public int mPackageN;// 心电包数
        public byte[] mPackageData;// 心电包数据
        public int mPlus;
    }

    private class DataModel {
        ArrayList<PackageModel> mPackets = new ArrayList<PackageModel>();

        public void add(PackageModel model) {
            synchronized (mPackets) {
                mPackets.add(model);
            }
        }

        public byte[] toBytes() {
            List<Byte> result = new ArrayList<Byte>();
            for (PackageModel model : mPackets) {
                for (byte v : model.mPackageData) {
                    result.add(v);
                }
            }

            byte[] resultByte = new byte[result.size()];
            for (int i = 0; i < result.size(); i++) {
                resultByte[i] = result.get(i);
            }

            return resultByte;

        }
    }

    public class PrtFtE100Model extends PrtData {
        public String mFilePath;// Dat文件路径
    }

    public PrtFt_E100() {
        super("");
        // TODO Auto-generated constructor stub
        mDataModel = new DataModel();
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

    public boolean sendConnectOk() {
        byte buff[] = onPackAsdu_UR_CNN_OK();
        return masterSnd(buff);
    }


    byte[] onPackAsdu_UR_SEARCH_OK() {
        mbSendBuffer = new byte[9];
        mbSendBuffer[0] = 0x4f;
        mbSendBuffer[1] = PDU_RU_ID;
        mbSendBuffer[2] = (byte) 0xff;
        mbSendBuffer[3] = (byte) 0xff;
        mbSendBuffer[4] = 0x03;
        mbSendBuffer[5] = (byte) 0xff;
        mbSendBuffer[6] = (byte) 0xff;
        mbSendBuffer[7] = (byte) 0xfc;
        mbSendBuffer[8] = checkSum(mbSendBuffer);
        return mbSendBuffer;
    }

    private byte[] onPackAsdu_UR_CNN_OK() {
        mbSendBuffer = new byte[9];
        mbSendBuffer[0] = 0x5f;
        mbSendBuffer[1] = PDU_UR_ID;
        mbSendBuffer[2] = mID[0];
        mbSendBuffer[3] = mID[1];
        mbSendBuffer[4] = 0x03;
        mbSendBuffer[5] = mID[2];
        mbSendBuffer[6] = mID[3];
        mbSendBuffer[7] = ASDU_PACK_CNN_OK;
        mbSendBuffer[8] = checkSum(mbSendBuffer);
        return mbSendBuffer;
    }

    private PackageModel onUnPackRealResult(byte[] pack) {
        PackageModel packet = new PackageModel();
        packet.mLenNo = (int) (pack[0] & 0x0F);
        packet.mPlus = (int) (pack[9]);

        int num = (int) (pack[4]);
        packet.mPackageData = new byte[num];
        System.arraycopy(pack, 10, packet.mPackageData, 0, num);

        return packet;
    }

    private boolean onUnPackDeviceID(byte[] pack) {
        if (pack.length != 8)
            return false;

        mID[0] = pack[2];
        mID[1] = pack[3];
        mID[2] = pack[5];
        mID[3] = pack[6];
        return true;
    }

    private boolean onUnPackTransmitOk(byte[] pack) {
        if (pack.length != 9)
            return false;

        if (pack[0] != 0x5F || pack[1] != 0x02) {
            return false;
        }

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

                if (data1 != (byte) PDU_UR_ID) {
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

                if (pck.length == 9) {
                    onUnPackDeviceID(pck);
                    sendConnectOk();//准备好接收数据， 数据传输完成
                    bTransmitOk = false;
                } else {
                    int pduCode = (int) (pck[7]);
                    switch (pduCode) {
                        case ASDU_PACK_CNN_OK: {
                            String filePath = this.storeData(mDataModel);
                            Log.i(TAG, "store DataModel filePath " + filePath);

                            PrtFtE100Model ecgModel = new PrtFtE100Model();
                            ecgModel.mFilePath = filePath;

                            if (mListener != null) {
                                if (filePath.isEmpty() == false) {
                                    PrtFt_E100Listener listener = (PrtFt_E100Listener) mListener;
                                    listener.onMasterReceive(CMD_ECGVALUE, ecgModel);
                                } else {
                                    mListener.onMasterErr();
                                }
                            }

                        }
                        break;
                        case ASDU_PACK_ERR:
                            break;
                        case ASDU_PACK_TIME_SYNC:
                            break;
                        case ASDU_PACK_TRANSMIT: {
                            PackageModel model = onUnPackRealResult(pck);
                            mDataModel.add(model);
                        }
                        break;
                        case ASDU_PACK_VERSION:
                            break;
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

    private String writePacketToDat(byte[] data) {
        try {
            if (!(Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED))) {
                FileOutputStream stream = mContext.openFileOutput(mFileName,
                        Context.MODE_PRIVATE);

                byte[] buf = data;
                stream.write(buf);
                stream.close();
                File fileDir = mContext.getFilesDir();
                return fileDir.getAbsolutePath();
            }

            String cacheDir = this.getCacheDir();
            File saveFile = new File(cacheDir, mFileName);

            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(data);
            outStream.close();
            return saveFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "File not found.");
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "File write error.");
            return "";
        }
    }

    private String getCacheDir() {
        String path = GlobalConstants.FILE_HEALTH_DIR.getAbsolutePath();
        return GlobalConstants.FILE_HEALTH_DIR.getAbsolutePath();
    }

    @SuppressLint("SimpleDateFormat")
    private String storeData(DataModel model) {
        byte[] dataStr = model.toBytes();

        if (dataStr == null) {
            return "";
        }

        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHH.mms");// 10012710.17
        mFileName = format.format(new Date(time));
        Log.e("info", "mFilieName=" + mFileName);
        return this.writePacketToDat(dataStr);
    }

    //TODO
    @SuppressLint("SimpleDateFormat")
    private String storeData() {
        byte[] dataStr = "hello world!".getBytes();

        if (dataStr == null) {
            return "";
        }

        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHH.mms");// 10012710.17
        mFileName = format.format(new Date(time));

        return this.writePacketToDat(dataStr);
    }
}

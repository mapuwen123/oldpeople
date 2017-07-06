package com.aiminerva.oldpeople.deviceservice.prt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.aiminerva.oldpeople.MyApplication;
import com.aiminerva.oldpeople.deviceservice.PrtBase;
import com.aiminerva.oldpeople.globalsettings.GlobalConstants;
import com.aiminerva.oldpeople.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ecganalysis.AnalysisResult;
import ecganalysis.Arrhythmia;
import ecganalysis.EcgAnalysis;

public class PrtFt_BP_88A extends PrtBase {
    //	public final static byte ID = 0x03;
    public final static String UDID = "finltop";//设备UDID
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtFt_BP_88A";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    private final static byte PDU_RU_ID = (byte) 0xff; //手机
    //FT_BP_88A 设备
    private final static byte PDU_UR_ID = 0x03; //测量单元

    private final static byte ASDU_PACK_CNN_OK = (byte) 0xfe;
    private final static byte ASDU_PACK_TIME_SYNC = (byte) 0x00;
    private final static byte ASDU_PACK_TRANSMIT = (byte) 0x03;
    private final static byte ASDU_PACK_VERSION = (byte) 0xfb;
    private final static byte ASDU_PACK_ERR = (byte) 0xff;

    //E100 设备
    private final static byte PDU_UE_ID = 0x02; //测量单元

    private final static byte ASDU_PACK_E_CNN_OK = (byte) 0xfe;
    private final static byte ASDU_PACK_E_TIME_SYNC = (byte) 0x00;
    private final static byte ASDU_PACK_E_TRANSMIT = (byte) 0x02;//心电数据包
    private final static byte ASDU_PACK_E_VERSION = (byte) 0xfb;
    private final static byte ASDU_PACK_E_ERR = (byte) 0xff;


    private PrtFTBloodPressModel mModel;
    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] mbSendBuffer;
    public byte[] mbRecBuffer;

    private boolean bTransmitOk = false;
    private byte[] mID = new byte[4];
    private int mDevType;

    @SuppressWarnings("serial")
    HashMap<String, Integer> TypeMaps = new HashMap<String, Integer>() {
        {
            put("ASY", 0);
            put("FIB", 1);
            put("VTA", 2);
            put("ROT", 3);
            put("RUN", 4);
            put("TPT", 5);
            put("CPT", 6);
            put("VPB", 7);
            put("BGM", 8);
            put("TGM", 9);
            put("TAC", 10);
            put("BRD", 11);
            put("MIS", 16);
            put("SB", 20);
            put("ST", 21);
            put("LRN", 22);
            put("NML", 24);
            put("NOS", 26);
            put("NON", 27);
        }
    };

    public interface PrtFt_SsdpBaseListenr {
        public void onDeviceTypeSsdp(PrtBase prt, int type);
    }

    public interface PrtFt_BP_88AListener extends PrtBaseListener {
        public boolean onMasterStart();

        public boolean onMasterEnd();

        public boolean onMasterReceive(byte command, PrtFTBloodPressModel result);
    }

    //define SugerPress model
    public class PrtFTBloodPressModel extends PrtData {
        public int mSystolicPressure;//收缩压
        public int mDiastolicPressure;//舒张压
        public int mPluse;//脉搏数/分
        public String mFilePath;
    }

    //define ECG model
    private DataModel mDataModel;
    private String mFileName = "";
    private Context mContext = MyApplication.getContext();

    private class PackageModel {
        public int mLenNo;// 心电条数No
        public int mPackageN;// 心电包数
        public byte[] mPackageData;// 心电包数据
        public int mPlus;
        public int qt;
        public int qrs;
        public int st;
    }

    private class DataModel {
        ArrayList<PackageModel> mPackets = new ArrayList<PackageModel>();

        public void add(PackageModel model) {
            mPackets.add(model);
        }

        private AnalysisResult ecgAnalysis() {
            int filterbuf[];
            EcgAnalysis ecgAnalys = new EcgAnalysis();
            ecgAnalys.EcgAnalysisInit(100, 0, 100);

            List<Integer> ecgArray = new ArrayList<Integer>();

            for (PackageModel model : mPackets) {
                for (byte v : model.mPackageData) {
                    ecgArray.add((int) v);
                }
            }
            int sourebuf[] = new int[ecgArray.size()];
            for (int i = 0; i < ecgArray.size(); i++) {
                sourebuf[i] = ecgArray.get(i);
            }
            filterbuf = ecgAnalys.EcgFilterMain(sourebuf, 3000);
            AnalysisResult result = ecgAnalys.EcgAnalysisMain2(filterbuf, 3000);
            return result;
        }

        public byte[] toBytes() {
            //获取ecg 心电列表参数
            AnalysisResult analysis = ecgAnalysis();

            List<Byte> result = new ArrayList<Byte>();

            if (mPackets.size() > 0) {
                PackageModel model = mPackets.get(mPackets.size() - 1);
                result.add((byte) (model.mPlus));
            }

            for (PackageModel model : mPackets) {
                for (byte v : model.mPackageData) {
                    result.add(v);
                }
            }
            //增加数据
            byte value0[] = Utils.int2byte(analysis.qt);
            for (int i = 0; i < 4; i++) {
                result.add(value0[i]);
            }
            byte value1[] = Utils.int2byte(analysis.qrs);
            for (int i = 0; i < 4; i++) {
                result.add(value1[i]);
            }
            byte value2[] = Utils.int2byte(analysis.st);
            for (int i = 0; i < 4; i++) {
                result.add(value2[i]);
            }

            byte value3[] = Utils.int2byte(analysis.arrcnt);
            for (int i = 0; i < 4; i++) {
                result.add(value3[i]);
            }

            for (int i = 0; i < analysis.arrcnt; i++) {
                Arrhythmia a = analysis.arr[i];
                byte value4[] = Utils.int2byte(a.warninglevel);
                for (int j = 0; j < 4; j++) {
                    result.add(value4[j]);
                }
                byte value5[] = Utils.int2byte(a.series);
                for (int j = 0; j < 4; j++) {
                    result.add(value5[j]);
                }

                byte value6[] = Utils.int2byte(TypeMaps.get(a.type));
                for (int j = 0; j < 4; j++) {
                    result.add(value6[j]);
                }
            }
            Log.d(TAG, "results size " + result.size());

            byte[] resultByte = new byte[result.size()];
            for (int i = 0; i < result.size(); i++) {
                resultByte[i] = result.get(i);
            }

            return resultByte;
        }
    }


    public PrtFt_BP_88A() {
        super("FT_BP_88A");
        // TODO Auto-generated constructor stub

    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        super.init();
        mRecvBuffer.clear();
        mDataModel = new DataModel();
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

    public boolean sendEConnectOk() {
        byte buff[] = onPackAsdu_UE_CNN_OK();
        return masterSnd(buff);
    }

    public boolean sendEOverConnectOk() {
        byte buff[] = onPackAsdu_UE_CNN_OVER_OK();
        return masterSnd(buff);
    }

    //血压
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

    private byte[] onPackAsdu_UR_CNN_OK() {
        mbSendBuffer = new byte[9];
        mbSendBuffer[0] = 0x4f;
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


    private byte[] onPackAsdu_UE_CNN_OK() {
        mbSendBuffer = new byte[9];
        mbSendBuffer[0] = 0x4f;
        mbSendBuffer[1] = PDU_UE_ID;
        mbSendBuffer[2] = mID[0];
        mbSendBuffer[3] = mID[1];
        mbSendBuffer[4] = 0x03;
        mbSendBuffer[5] = mID[2];
        mbSendBuffer[6] = mID[3];
        mbSendBuffer[7] = ASDU_PACK_E_CNN_OK;
        mbSendBuffer[8] = checkSum(mbSendBuffer);
        return mbSendBuffer;
    }

    private byte[] onPackAsdu_UE_CNN_OVER_OK() {
        mbSendBuffer = new byte[9];
        mbSendBuffer[0] = 0x5f;
        mbSendBuffer[1] = PDU_UE_ID;
        mbSendBuffer[2] = mID[0];
        mbSendBuffer[3] = mID[1];
        mbSendBuffer[4] = 0x03;
        mbSendBuffer[5] = mID[2];
        mbSendBuffer[6] = mID[3];
        mbSendBuffer[7] = ASDU_PACK_E_CNN_OK;
        mbSendBuffer[8] = checkSum(mbSendBuffer);
        return mbSendBuffer;
    }

    private PrtFTBloodPressModel onUnPackRealResult(byte[] pack) {
        PrtFTBloodPressModel model = new PrtFTBloodPressModel();

        byte plus_hb = (byte) (pack[15] & 0x08);
        byte avgpress_hb = (byte) (pack[15] & 0x04);
        byte systolicpress_hb = (byte) (pack[15] & 0x01);
        byte diastolicpress_hb = (byte) (pack[15] & 0x02);

        byte plus_lb = pack[19];
        model.mDiastolicPressure = (short) diastolicpress_hb * 256 + (short) pack[17];
        model.mSystolicPressure = (short) systolicpress_hb * 256 + (short) pack[16];
        model.mPluse = (short) plus_hb * 256 + (short) pack[19];
        return model;
    }

    private boolean onUnPackDeviceID(byte[] pack) {
        if (pack.length != 8) {
            Log.e(TAG, "ssdp packet length != 8");
            return false;
        }

        mDevType = (int) pack[1];
        mID[0] = pack[2];
        mID[1] = pack[3];
        mID[2] = pack[5];
        mID[3] = pack[6];
        return true;
    }

    private boolean onUnPackTransmitOk(byte[] pack) {
        if (pack.length != 9)
            return false;

        if (pack[0] != 0x5F || pack[1] != 0x03) {
            return false;
        }

        return true;
    }

    private PackageModel onUnPackE100RealResult(byte[] pack) {
        PackageModel packet = new PackageModel();
        packet.mLenNo = (int) (pack[0] & 0x0F);
        packet.mPlus = (int) (pack[10]);

        int num = (int) (pack[4]) - 6;
        packet.mPackageData = new byte[num];
        System.arraycopy(pack, 10, packet.mPackageData, 0, num);

        return packet;
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
            int length = mRecvBuffer.limit() - mRecvBuffer.position();

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

                if (data1 != (byte) PDU_UR_ID && data1 != (byte) PDU_UE_ID) {
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
                    if (!onUnPackDeviceID(pck)) {
                        Log.d(TAG, "ssdp protal length is error");
                        return false;
                    }

                    //获取设备类型
                    Log.d(TAG, "protal is type = " + mDevType);
                    if (mDevType == PDU_UR_ID) {//血压
                        sendConnectOk();//准备好接收数据， 数据传输完成
                    } else if (mDevType == PDU_UE_ID) { //心电
                        sendEConnectOk();//准备好接收数据， 数据传输完成
                    }

                    if (mListener != null) {
                        PrtFt_BP_88AListener listener = (PrtFt_BP_88AListener) mListener;
                        listener.onMasterStart();
                    }
                    bTransmitOk = false;
                } else {
                    if (mDevType == PDU_UR_ID && pck[1] == PDU_UR_ID) {// 血压
                        int pduCode = (int) (pck[7]);
                        switch (pduCode) {
                            case ASDU_PACK_CNN_OK: {

                                if (mListener != null) {
                                    PrtFt_BP_88AListener listener = (PrtFt_BP_88AListener) mListener;
                                    // if (!bTransmitOk){
                                    // listener.onMasterStart();
                                    // }else{
                                    listener.onMasterReceive(CMD_BLOODPRESSVALUE,
                                            mModel);
                                    listener.onMasterEnd();
                                    // }
                                    bTransmitOk = !bTransmitOk;
                                }
                            }
                            break;
                            case ASDU_PACK_ERR:
                                break;
                            case ASDU_PACK_TIME_SYNC:
                                break;
                            case ASDU_PACK_TRANSMIT:
                                mModel = onUnPackRealResult(pck);
                                break;
                            case ASDU_PACK_VERSION:
                                break;
                        }
                    } else if (mDevType == PDU_UE_ID && pck[1] == PDU_UE_ID) {//心电
                        int pduCode = (int) (pck[7]);
                        switch (pduCode) {
                            case ASDU_PACK_E_CNN_OK: {

                                String filePath = this.storeData(mDataModel);
                                Log.i(TAG, "store DataModel filePath " + filePath);

                                PrtFTBloodPressModel ecgModel = new PrtFTBloodPressModel();
                                ecgModel.mFilePath = filePath;

                                if (mListener != null) {
                                    if (filePath.isEmpty() == false) {
                                        PrtFt_BP_88AListener listener = (PrtFt_BP_88AListener) mListener;
                                        listener.onMasterReceive(CMD_ECGVALUE, ecgModel);
                                    } else {
                                        mListener.onMasterErr();
                                    }
                                }

                            }
                            break;
                            case ASDU_PACK_E_ERR:
                                //重新探查？？
                                break;
                            case ASDU_PACK_E_TIME_SYNC:
                                break;
                            case ASDU_PACK_E_TRANSMIT: {
                                PackageModel model = onUnPackE100RealResult(pck);
                                mDataModel.add(model);
                            }
                            break;
                            case ASDU_PACK_E_VERSION:
                                break;
                        }
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
        mFileName = "flintop_" + format.format(new Date(time));
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


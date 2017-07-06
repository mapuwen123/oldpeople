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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//心电协议解析
public class PrtEtc_HC201B extends PrtBase {


    public final static String UDID = "ECG:HC-201B";
    public final static String PASSWORD = "1234";

    private final static String TAG = "PrtEtc_HC201B";

    private final static int MAX_SEND_SIZE = 1024;
    private final static int MAX_RECV_SIZE = 1024;

    public byte[] mbSendBuffer = new byte[MAX_SEND_SIZE];
    // public byte[] mbRecBuffer = new byte[MAX_RECV_SIZE];
    public ByteBuffer mRecvBuffer = ByteBuffer.allocate(65535);

    public byte[] ASDU_ACK_Hello_regularData = {0x55, 0x01, 0x01, (byte) 0xaa,
            0x0a};
    public byte[] ASDU_ACKTotalLength = {0x55, 0x02, 0x00, 0x00, 0x0a};

    private Context mContext = MyApplication.getContext();
    private DataModel mDataModel;
    private String mFileName = "";

    private int mRecvNum = 0;// 缓冲区数据长度
    private int mPackageLen = 0;//
    private int timeN;
    private int startIndex = 0;
    private int mPackageLenTotal = 0;
    private int mCurrentPackageTotal = 0;
    private int mCurrentPackageNo = 0;
    private int mCurrentPackageLen = 0;
    private byte mCurrentPackageType = 0x03;

    public interface PrtEtc_HC201BListener extends PrtBaseListener {
        public boolean onMasterReceive(byte command, PrtECGModel result);
    }

    public class PrtECGModel extends PrtData {
        public String mFilePath;// Dat文件路径
    }

    private class PackageModel {
        public int mLenNo;// 心电条数
        public int mPackageN;// 心电包数
        public byte[] mPackageData;// 心电包数据
    }

    // 数据包Model
    private class DataModel {

        Map<Integer, Map<Integer, PackageModel>> mData = new TreeMap<Integer, Map<Integer, PackageModel>>(
                new Comparator<Integer>() {
                    public int compare(Integer obj1, Integer obj2) {
                        // ��������
                        return obj1.compareTo(obj2);
                    }
                });

        public void add(int len, int packageNo, PackageModel model) {
            Map<Integer, PackageModel> obj = mData.get(len);

            if (obj == null) {
                obj = new TreeMap<Integer, PackageModel>(
                        new Comparator<Integer>() {
                            public int compare(Integer obj1, Integer obj2) {
                                // 升序排列
                                return obj1.compareTo(obj2);
                            }
                        });

                mData.put(len, obj);
            }
            obj.put(packageNo, model);
        }

        public int getLenN() {
            return mData.size();
        }

        public int getPackageN(int len) {
            Map<Integer, PackageModel> obj = mData.get(len);
            if (obj == null) {
                return 0;
            }
            return obj.size();
        }

        public byte[] toBytes() {
            List<Byte> result = new ArrayList<Byte>();

            for (Map.Entry<Integer, Map<Integer, PackageModel>> entry : mData
                    .entrySet()) {
                Map<Integer, PackageModel> value = entry.getValue();

                for (Map.Entry<Integer, PackageModel> entryModel : value
                        .entrySet()) {

                    byte[] source = entryModel.getValue().mPackageData;
                    List<Byte> b = new ArrayList<Byte>();
                    for (int i = 0; i < source.length; i++) {
                        b.add(source[i]);
                    }

                    result.addAll(b);
                }
            }

            byte[] resultByte = new byte[result.size()];
            for (int i = 0; i < result.size(); i++) {
                resultByte[i] = result.get(i);
            }

            return resultByte;
        }
    }

    public PrtEtc_HC201B() {
        // TODO Auto-generated constructor stub
        super(UDID);
    }

    public void init() {
        mRecvNum = 0;
        timeN = 0;

        startIndex = 0;
        mPackageLen = 0;
        // mPackageN = 0;

        //TODO Test
        //String s = storeData() ;
    }

    @Override
    public void uinit() {
        // TODO Auto-generated method stub
        super.uinit();
        mRecvBuffer.clear();
    }

    public synchronized boolean masterRec(byte[] buffer) {
        Log.i("info", "===masterRec===");
        if (buffer == null) {
            return false;
        }

        mRecvBuffer.put(buffer);

        mRecvBuffer.flip();
        Log.e("limit", "limit " + mRecvBuffer.limit() + "position "
                + mRecvBuffer.position());

        while (0 < (mRecvBuffer.limit() - mRecvBuffer.position())) {
            int mm = mRecvBuffer.get(mRecvBuffer.position());
            byte data = (byte) (mRecvBuffer.get(mRecvBuffer.position()) & 0xFF);// 不改边postion位置
            byte data1 = 0x00;
            int length = mRecvBuffer.limit() - mRecvBuffer.position();

            if (length < 2) {
                byte[] remainBuffer = new byte[length];
//				Log.e(TAG, "RecvBuffer  transfering ...");

                mRecvBuffer.get(remainBuffer);
                mRecvBuffer.clear();

                mRecvBuffer.put(remainBuffer);

//				Log.e(TAG, "RecvBuffer  transfered");

                return false;
            } else if (data == (byte) 0xaa) {
                data1 = mRecvBuffer.get(mRecvBuffer.position() + 1);// 不改边postion位置
            } else {
                mRecvBuffer.get();// position++
                continue;
            }


            if (data == (byte) 0xaa && data1 == (byte) 0x01) {

                Log.e(TAG, "RecvBuffer packet 1 !");
                if (length < 4) {

                    byte[] remainBuffer = new byte[length];
                    Log.e(TAG, "RecvBuffer  transfering ...");

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();

                    mRecvBuffer.put(remainBuffer);

                    Log.e(TAG, "RecvBuffer  transfered");

                    return false;

                } else {
                    Log.e(TAG, "send packet ASDU_ACK_Hello_regularData !");
                    byte[] pck = new byte[4];
                    mRecvBuffer.get(pck, mRecvBuffer.position(), 4);
//					mRecvBuffer.get(pck);// postion

                    // ACK_Hello_regularData
                    this.masterSend(pack_ASDU_ACK_Hello_regularData());
                    timeN = 1;

                    continue;
                }
            } else if (data == (byte) 0xaa && data1 == (byte) 0x02) {

                if (length < 4) {

                    byte[] remainBuffer = new byte[length];
                    Log.e(TAG, "RecvBuffer  transfering ...");

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();

                    mRecvBuffer.put(remainBuffer);

                    Log.e(TAG, "RecvBuffer  transfered");

                    return false;

                } else {
                    byte[] pck = new byte[4];
                    mRecvBuffer.get(pck, mRecvBuffer.position(), 4);
//					mRecvBuffer.get(pck);// postion

                    // ACKTotalLength
                    mPackageLenTotal = pck[2]
                            + pck[3] << 8;
                    this.masterSend(pack_ASDU_ACKTotalLength());
                    timeN = 2;

                    continue;
                }

            } else if (data == (byte) 0xaa
                    && (data1 == (byte) 0x03 || data1 == (byte) 0x04)
                    && timeN == 2) {

                if (length < 4) {

                    byte[] remainBuffer = new byte[length];
                    Log.e(TAG, "RecvBuffer  transfering ...");

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();

                    mRecvBuffer.put(remainBuffer);

                    Log.e(TAG, "RecvBuffer  transfered");

                    return false;

                } else {
                    // 处理第1包数据
                    timeN = 3;
                    mDataModel = new DataModel();
                    continue;
                }
            } else if (data == (byte) 0xaa
                    && (data1 == (byte) 0x03 || data1 == (byte) 0x04)
                    && timeN == 3) {

                if (length < 10) {

                    byte[] remainBuffer = new byte[length];
                    Log.e(TAG, "RecvBuffer  transfering ...");

                    mRecvBuffer.get(remainBuffer);
                    mRecvBuffer.clear();

                    mRecvBuffer.put(remainBuffer);

                    Log.e(TAG, "RecvBuffer  transfered");

                    return false;

                } else {
                    // 获取包长度
                    mPackageLen = (short) (mRecvBuffer.get(mRecvBuffer.position() + 2)) & 0x00ff
                            + ((short) (mRecvBuffer.get(mRecvBuffer.position() + 3) & 0x00ff) << 8);

                    mCurrentPackageTotal = (short) (mRecvBuffer.get(mRecvBuffer
                            .position() + 4)) & 0x00ff
                            + ((short) (mRecvBuffer.get(mRecvBuffer.position() + 5) & 0x00ff) << 8);
                    mCurrentPackageNo = (short) (mRecvBuffer
                            .get(mRecvBuffer.position() + 6)) & 0x00ff
                            + ((short) (mRecvBuffer.get(mRecvBuffer.position() + 7) & 0x00ff) << 8);
                    mCurrentPackageLen = (short) (mRecvBuffer
                            .get(mRecvBuffer.position() + 8)) & 0x00ff
                            + ((short) (mRecvBuffer.get(mRecvBuffer.position() + 9) & 0x00ff) << 8);
                    mCurrentPackageType = mRecvBuffer.get(mRecvBuffer
                            .position() + 1);

                    Log.i(TAG, "recv package lenth" + mPackageLen
                            + " package total " + mCurrentPackageTotal
                            + "current package no" + mCurrentPackageNo
                            + "current package lenth " + mCurrentPackageLen);
                    Log.i(TAG, "RecvBuffer length " + length + " first data " + data);

                    Log.e(TAG, "limit " + mRecvBuffer.limit() + "position "
                            + mRecvBuffer.position());
                    Log.e(TAG, "package len " +
                            "data0 " + mRecvBuffer.get(mRecvBuffer.position() + 0) +
                            "data1 " + mRecvBuffer.get(mRecvBuffer.position() + 1) +
                            "data2 " + mRecvBuffer.get(mRecvBuffer.position() + 2) +
                            "data3 " + mRecvBuffer.get(mRecvBuffer.position() + 3));

                    if (length < mCurrentPackageLen + 10) {

                        byte[] remainBuffer = new byte[length];
                        Log.e(TAG, "RecvBuffer  transfering ...");

                        mRecvBuffer.get(remainBuffer);
                        mRecvBuffer.clear();

                        mRecvBuffer.put(remainBuffer);

                        Log.e(TAG, "RecvBuffer  transfered");

                        return false;

                    } else {
                        // store Data
                        byte[] DataRegular_Buffer = new byte[10];
                        mRecvBuffer.get(DataRegular_Buffer);// position

                        PackageModel model = new PackageModel();
                        model.mPackageData = new byte[mCurrentPackageLen];
                        mRecvBuffer.get(model.mPackageData);// position

                        model.mLenNo = mPackageLen;
                        model.mPackageN = mCurrentPackageNo;

                        // ACK_Data
                        this.masterSend(pack_ACK_Data(mCurrentPackageType,
                                (short) mCurrentPackageNo));
                        Log.i(TAG, "send ACK_DATA package no " + mCurrentPackageNo);

                        if (1 == mPackageLen) {
                            mDataModel.add(mPackageLen, mCurrentPackageNo, model);
                            Log.i(TAG, "store packet no " + mCurrentPackageNo);
                        } else {
                            timeN = 0;
                            break;
                        }

                        if (mDataModel.getPackageN(mPackageLen) == mCurrentPackageTotal) {
                            String filePath = this.storeData(mDataModel);
                            Log.i(TAG, "store DataModel filePath " + filePath);
                            timeN = 0;

                            PrtECGModel ecgModel = new PrtECGModel();
                            ecgModel.mFilePath = filePath;

                            if (mListener != null) {
                                if (filePath.isEmpty() == false) {
                                    PrtEtc_HC201BListener listener = (PrtEtc_HC201BListener) mListener;
                                    listener.onMasterReceive(CMD_ECGVALUE, ecgModel);
                                } else {
                                    mListener.onMasterErr();
                                }
                            }

                            break;
                        }
                        continue;
                    }
                }
            } else {
                mRecvBuffer.get();
            }
        }

        // reset ByteBuffer
        mRecvBuffer.clear();
        return true;
    }

    public synchronized boolean masterSend(byte[] buffer) {
        if (mListener != null) {
            return mListener.onMasterSend(buffer);
        }
        return false;
    }

    byte[] pack_ASDU_ACKTotalLength() {
        return ASDU_ACKTotalLength;
    }

    byte[] pack_ASDU_ACK_Hello_regularData() {
        return ASDU_ACK_Hello_regularData;
    }

    byte[] pack_ACK_Data(byte dataType, short packetNum) {

        byte[] buffer = new byte[5];
        buffer[0] = 0x55;
        buffer[1] = dataType;
        buffer[2] = (byte) (packetNum & 0x00FF);
        buffer[3] = (byte) (packetNum >> 8);
        buffer[4] = 0x0a;

        return buffer;
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

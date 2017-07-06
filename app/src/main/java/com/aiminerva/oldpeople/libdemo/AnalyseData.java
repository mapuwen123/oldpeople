package com.aiminerva.oldpeople.libdemo;

/**
 * Created by Aaron on 2017/2/16.
 */

import com.creative.base.BYTEO;
import com.creative.base.BaseDate;
import com.creative.base.Ianalyse;

import java.util.Vector;

public class AnalyseData extends BaseThread
        implements Ianalyse
{
    private static final int TOKEN_INQUIRE = 17;
    private static final int TOKEN_SYNCTIME = 51;
    private static final int TOKEN_TRANSFERSET = 85;
    private static final int TOKEN_TRANSFERFILE = 102;
    private static final int TOKEN_TRANSFERDATA = 170;
    private static final int TOKEN_PREPARE = 221;
    private static final int TOKEN_HEAD = 255;
    private boolean is5Ver = false;
    private IECGCallBack callBack;
    private int frameNum = -1;

    private int paracnt = 0;

    private long pretime = 0L;

    private Vector<Vector<Integer>> tempFileDataIntegers = null;
    private BaseThread timeOutTh;
    private int timeOutCnt;

    public AnalyseData(IECGCallBack callBack)
    {
        this.callBack = callBack;
    }

    public void run()
    {
        super.run();
        try {
            while (!this.stop) {
                synchronized (this) {
                    if (this.pause) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (Receive.originalData.size() > 0)
                        analyse();
                    else
                        Thread.sleep(50L);
                }
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public void analyse()
            throws Exception
    {
        int cnt = Verifier.checkIntactCnt(Receive.originalData);
        int temp1 = -1; int temp2 = -1;
        try {
            int token = 0;
            int len = 0;
            for (int i = 0; i < cnt; i++) {
                temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                token = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                len = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                int AI;
                switch (token) {
                    case 17:
                        com.creative.base.BaseDate.isReadVer = true;
                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue();
                        int SA = BYTEO.getH4((byte)temp1);
                        int SI = BYTEO.getL4((byte)temp1);
                        temp2 = ((Byte)Receive.originalData.remove(0)).byteValue();
                        int HA = BYTEO.getH4((byte)temp2);
                        int HI = BYTEO.getL4((byte)temp2);
                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue();
                        int AA = BYTEO.getH4((byte)temp1);
                        AI = BYTEO.getL4((byte)temp1);
                        this.callBack.OnGetDeviceVer(HA, HI, SA, SI, AA, AI);

                        break;
                    case 51:
//                        PC80BSendCMDThread.sendTIME();
                        for (int j = 0; j < len; j++) {
                            Receive.originalData.remove(0);
                        }

                        break;
                    case 85:
                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue();
                        if (temp1 == 10)
                            StatusMsg.rDeviceName = "PC-80A";
                        else if (temp1 == 11)
                            StatusMsg.rDeviceName = "PC-80B";
                        else if (temp1 == 128) {
                            StatusMsg.rDeviceName = "PC-80B(UW)";
                        }
                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                        int nSmoothingMode = temp1 & 0x80;
                        int nTransMode = temp1 & 0x1;
                        StatusMsg.SENDMODE = nTransMode;
                        StatusMsg.LeakPack.clear();
                        byte[] ID = new byte[12];
                        for (int j = 0; j < len - 2; j++) {
                            ID[j] = ((Byte)Receive.originalData.remove(0)).byteValue();
                        }
                        if (nTransMode == 1) {
                            timerOfTimeOut(true);
                            this.tempFileDataIntegers = new Vector();
                            this.tempFileDataIntegers.setSize(200);
                            this.callBack.OnGetFileTransmit(0, null);
                        }
                        this.callBack.OnGetRequest(StatusMsg.rDeviceName, new String(ID), nSmoothingMode, nTransMode);
//                        PC80BSendCMDThread.sendSetACK();

                        break;
                    case 102:
                        if (len == 2) {
                            temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                            Receive.originalData.remove(0);
                            if (temp1 != 1) break;
                            StatusMsg.SENDMODE = 1;
                            timerOfTimeOut(true);
                            this.tempFileDataIntegers = new Vector();
                            this.tempFileDataIntegers.setSize(200);
                            this.callBack.OnGetFileTransmit(0, null);
//                            PC80BSendCMDThread.sendSetACK();
                        } else {
                            if (len != 1) break;
                            temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                            if (temp1 != 0) break;
                            this.is5Ver = true;
                        }

                        break;
                    case 170:
                        int fr = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
//                        if (this.frameNum == fr - 1)
//                            PC80BSendCMDThread.sendDataACK(fr);
//                        else {
//                            do
//                                PC80BSendCMDThread.sendDataNAK(++this.frameNum);
//                            while (this.frameNum != fr);
//                        }

                        this.frameNum = fr;
                        int lead;
                        if (len > 1) {
                            if (StatusMsg.SENDMODE == 0) {
                                BaseDate.ECGData data = new BaseDate.ECGData();
                                data.frameNum = fr;
                                for (int j = 0; j < (len - 4) / 2; j++) {
                                    temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    temp2 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    temp1 = ((temp2 & 0xF) << 8) + temp1;
                                    data.data.add(new BaseDate.Wave(temp1, 0));
                                }
                                int nHR = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                temp2 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;

                                lead = (temp2 & 0x80) >>> 7;
                                int nPower = ((temp2 & 0xF) << 8) + temp1;
                                this.callBack.OnGetRealTimeMeasure(lead == 1, data, 0, nHR,
                                        nPower, 2); } else {
                                if (StatusMsg.SENDMODE != 1) break;
                                try {
                                    this.timeOutCnt = 0;
                                    Vector tmp = new Vector();
                                    for (int j = 0; j < len - 1; j++) {
                                        tmp.add(Integer.valueOf(((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF));
                                    }
                                    this.tempFileDataIntegers.set(fr, tmp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    this.callBack.OnGetFileTransmit(3, null);
                                }
                            }
                        }
                        else {
                            this.frameNum = -1;
                            if (StatusMsg.SENDMODE == 0) {
                                this.callBack.OnGetRealTimeResult(null, StatusMsg.SENDMODE, 0, 0);
                            } else {
                                timerOfTimeOut(false);
//                                PC80BSendCMDThread.sendDataACK(fr);
                                Vector tmp = new Vector();
                                for (Vector vec : this.tempFileDataIntegers) {
//                                    if (vec != null) {
//                                        for (Integer integer : vec) {
//                                            tmp.add(integer);
//                                        }
//                                    }
                                }
                                System.out.println("发送完成：" + tmp.size());
                                if (tmp.size() == 9796)
                                    this.callBack.OnGetFileTransmit(1, tmp);
                                else {
                                    this.callBack.OnGetFileTransmit(3, null);
                                }
                                this.tempFileDataIntegers.clear();
                                this.tempFileDataIntegers = null;
                            }
                            StatusMsg.SENDMODE = 3;
                        }

                        break;
                    case 221:
                        StatusMsg.SENDMODE = 2;
                        /*int */fr = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                        this.paracnt += 1;

//                        PC80BSendCMDThread.sendDataACK(fr);

                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                        temp2 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                        int gain = (temp2 & 0x70) >>> 4;
                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;

                        int stage = temp1 & 0xF;

                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                        temp2 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;

                        /*int*/ lead = (temp2 & 0x80) >>> 7;
                        int ecg = temp2 & 0x3;
                        switch (ecg) {
                            case 0:
                                StatusMsg.SENDMODE = 0;
                                if (System.currentTimeMillis() - this.pretime > 500L) {
                                    if (((stage == 5) || (stage == 3)) &&
                                            (this.paracnt != 271)) {
                                        this.callBack.OnReceiveTimeOut(1);
                                    }

                                    this.pretime = System.currentTimeMillis();
                                }
                                this.paracnt = 0;
                                break;
                            case 1:
                                BaseDate.ECGData data = new BaseDate.ECGData();
                                data.frameNum = fr;
                                for (int j = 0; j < (len - 6) / 2; j++) {
                                    temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    temp2 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    temp1 = ((temp2 & 0xF) << 8) + temp1;
                                    data.data.add(new BaseDate.Wave(temp1, 0));
                                }
                                if (stage < 2) {
                                    this.callBack.OnGetRealTimePrepare(lead == 1, data, gain); } else {
                                    if (stage != 2) break;
                                    this.callBack.OnGetRealTimeMeasure(lead == 1, data, 2, 0, 0, gain);
                                }
                                break;
                            case 2:
                                if (stage != 4) {
                                    for (int j = 0; j < len - 6; j++)
                                        temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                }
                                else
                                {
                                    String year = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue()) +
                                            Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    String month = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    if (Integer.valueOf(month).intValue() < 10) {
                                        month = "0" + month;
                                    }
                                    String day = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    if (Integer.valueOf(day).intValue() < 10) {
                                        day = "0" + day;
                                    }
                                    String hour = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    if (Integer.valueOf(hour).intValue() < 10) {
                                        hour = "0" + hour;
                                    }
                                    String minute = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    if (Integer.valueOf(minute).intValue() < 10) {
                                        minute = "0" + minute;
                                    }
                                    String second = Integer.toHexString(((Byte)Receive.originalData.remove(0)).byteValue());
                                    if (Integer.valueOf(second).intValue() < 10) {
                                        second = "0" + second;
                                    }
                                    String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                                    temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    int nResult = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                    this.callBack.OnGetRealTimeResult(time, 2, nResult, temp1);
                                }break;
                            default:
                                for (int j = 0; j < len - 6; j++) {
                                    temp1 = ((Byte)Receive.originalData.remove(0)).byteValue() & 0xFF;
                                }

                        }

                        break;
                    case 255:
                        int power = ((Byte)Receive.originalData.remove(0)).byteValue() & 0x3;
                        this.callBack.OnGetPower(power);
                }

                Receive.originalData.remove(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Vector<Integer> computeFrame(int frame)
    {
        Vector frames = new Vector();
        if (this.frameNum == frame) {
            this.frameNum = ((this.frameNum + 1) % 256);
            return frames;
        }
        if (StatusMsg.SENDMODE == 1) {
            for (int i = this.frameNum; i < frame; i++) {
                frames.add(Integer.valueOf(i));
            }
        }
        return frames;
    }

    private void timerOfTimeOut(boolean isStart)
    {
        if (isStart) {
            if (this.timeOutTh == null) {
                this.timeOutTh = new BaseThread()
                {
                    public void run() {
                        super.run();
                        while (!this.stop) {
                            AnalyseData.this.timeOutCnt += 1;
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (AnalyseData.this.timeOutCnt == 3) {
                                AnalyseData.this.timeOutCnt = 0;
                                AnalyseData.this.timeOutTh = null;
                                Stop();
                                AnalyseData.this.callBack.OnReceiveTimeOut(0);
                                if (AnalyseData.this.tempFileDataIntegers != null) {
                                    AnalyseData.this.tempFileDataIntegers.clear();
                                    AnalyseData.this.tempFileDataIntegers = null;
                                }
                            }
                        }
                    }
                };
                this.timeOutTh.start();
            }
        } else {
            if (this.timeOutTh != null) {
                this.timeOutTh.Stop();
            }
            this.timeOutTh = null;
            this.timeOutCnt = 0;
        }
    }
}
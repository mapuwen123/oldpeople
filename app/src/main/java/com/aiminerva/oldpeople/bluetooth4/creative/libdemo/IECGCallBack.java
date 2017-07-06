package com.aiminerva.oldpeople.bluetooth4.creative.libdemo;

/**
 * Created by Aaron on 2017/2/16.
 */

import com.creative.base.BaseDate;

import java.util.Vector;

public abstract interface IECGCallBack
{
    public abstract void OnGetDeviceVer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);

    public abstract void OnGetRequest(String paramString1, String paramString2, int paramInt1, int paramInt2);

    public abstract void OnGetFileTransmit(int paramInt, Vector<Integer> paramVector);

    public abstract void OnGetRealTimePrepare(boolean paramBoolean, BaseDate.ECGData paramECGData, int paramInt);

    public abstract void OnGetRealTimeMeasure(boolean paramBoolean, BaseDate.ECGData paramECGData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    public abstract void OnGetRealTimeResult(String paramString, int paramInt1, int paramInt2, int paramInt3);

    public abstract void OnGetPower(int paramInt);

    public abstract void OnReceiveTimeOut(int paramInt);

    public abstract void OnConnectLose();
}
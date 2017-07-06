package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.bean.Poinots;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC201B;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

public class ETC_HC201Service extends BaseHealthService implements PrtEtc_HC201B.PrtEtc_HC201BListener {

    // --reponse data
    public PrtEtc_HC201B.PrtECGModel repECGModel;
    private String TAG = "ETC_HC201Service";
    private String userid = "";

    public interface ETC_HC201ServiceListener extends BaseHealthServiceListener {
        void onUpdateECGFromList(List<Poinots> data);

        void onUpdateECGFromFile(String path);

        void onUpdateError(ETC_HC201Service sevice, String sErr);
    }

    public void getUserId() {

    }


    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        super.mPrtObject = new PrtEtc_HC201B();
        super.mPrtObject.setListener(this);
        super.DEVICE_NAME = "心电仪";
        Log.v(TAG, "init success!");
        return super.init();
    }

    @Override
    public void uinit() {
        // TODO Auto-generated method stub
        super.uinit();
    }

    @Override
    public void onChatMsgRecv(BluetoothChatService blueToothChat, byte[] recv) {
        // TODO Auto-generated method stub
        super.onChatMsgRecv(blueToothChat, recv);

        PrtEtc_HC201B prtObject = (PrtEtc_HC201B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

//		PrtEtc_HC201B prtObject = (PrtEtc_HC201B) super.mPrtObject;
//		prtObject.masterSend(send);
    }

    public boolean onMasterReceive(byte command, PrtEtc_HC201B.PrtECGModel result) {
        // TODO Auto-generated method stub 此处需要上传ECG文件，心电图文件
        Logger.i("onMasterReceive====file is===" + result.mFilePath);
        File ecgfile = new File(result.mFilePath);
        if (ecgfile.exists()) {
            parpareUpload(result.mFilePath);

        }

        return true;
    }

    @Override
    public void onConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onConnected(blueToothChat);
        Log.v(TAG, "connected!");
    }

    @Override
    public void onConnectFailed(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onConnectFailed(blueToothChat);
        Log.v(TAG, "connect failed!");
    }

    @Override
    public void onDisConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onDisConnected(blueToothChat);
        Log.v(TAG, "disconnect !");
    }


    private void parpareUpload(final String path) {
        final List<BaseHealthServiceListener> listeners = mListeners;
        if (listeners.size() <= 0) {
            return;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof ETC_HC201ServiceListener) == true) {
                        ETC_HC201ServiceListener etcListener = (ETC_HC201ServiceListener) listener;
                        etcListener.onUpdateECGFromFile(path);
                    }
                }
            }

        };
        h.sendEmptyMessage(1);
    }

}

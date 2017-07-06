package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.prt.PrtFinltopDiscovery;

import java.util.List;

public class FT_FinltopService extends BaseHealthService implements PrtFinltopDiscovery.PrtFt_FinltopDiscoveryListener {

    // --reponse data
    private String TAG = "FT_FinltopService";

    public interface FT_FinltopServiceListener extends BaseHealthServiceListener {
        public void onFinltopDiscovery(FT_FinltopService service, int type);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtFinltopDiscovery();
        super.mPrtObject.setListener(this);
        super.DEVICE_NAME = "finltop搜索";

        Log.v(TAG, "init success!");
        return true;
    }

    @Override
    public void uinit() {
        // TODO Auto-generated method stub
        super.uinit();
    }

    @Override
    public void onConnected(BluetoothChatService blueToothChat) {
        super.onConnected(blueToothChat);

        PrtFinltopDiscovery prtObject = (PrtFinltopDiscovery) super.mPrtObject;
        prtObject.sendSsdp();
    }

    @Override
    public void onChatMsgRecv(BluetoothChatService blueToothChat, byte[] recv) {
        // TODO Auto-generated method stub
        super.onChatMsgRecv(blueToothChat, recv);

        PrtFinltopDiscovery prtObject = (PrtFinltopDiscovery) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);
    }

    @Override
    public boolean onMasterSend(byte[] buf) {
        mBluetoothChat.write(buf);
        return true;
    }

    @Override
    public boolean onMasterReceive(byte command, PrtFinltopDiscovery.PrtFT_FinltopDevModel result) {
        // TODO Auto-generated method stub

        final List<BaseHealthServiceListener> listeners = super.mListeners;

        if (listeners.size() <= 0) {
            return true;
        }
        final PrtFinltopDiscovery.PrtFT_FinltopDevModel model = result;
        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof FT_FinltopServiceListener) == true) {
                        FT_FinltopServiceListener ftListener = (FT_FinltopServiceListener) listener;
                        ftListener.onFinltopDiscovery(FT_FinltopService.this, model.mType);
                    }

                }
            }
        };
        h.sendEmptyMessage(1);
        return true;
    }

}

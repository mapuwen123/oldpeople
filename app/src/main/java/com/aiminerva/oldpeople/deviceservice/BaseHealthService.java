/**
 *
 */
package com.aiminerva.oldpeople.deviceservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC503B;
import com.aiminerva.oldpeople.deviceservice.prt.PrtFt_BP_88A;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Administrator
 */
public abstract class BaseHealthService implements BluetoothChatServiceListener, PrtBase.PrtBaseListener {
    static final String TAG = "BaseHealthService";
    public String DEVICE_NAME = "";

    public interface BaseHealthServiceListener {
        public void onConnecting(BaseHealthService service);

        public void onConnected(BaseHealthService service);

        public void onDisConnected(BaseHealthService service);

        public void onConnectFailed(BaseHealthService service);

        //public void onDiagonsised(JPushManager manager, Object state );
        //public void onFollowuped(JPushManager manager, Object state );
    }

    private Boolean mAutoConnect;

    protected ArrayList<BaseHealthServiceListener> mListeners;
    protected BluetoothChatService mBluetoothChat;
    protected BluetoothDevice mBluetoothDevice;
    protected PrtBase mPrtObject;
    protected Looper mLooper;

    protected BaseHealthService() {
        mAutoConnect = false;
        mListeners = new ArrayList<BaseHealthServiceListener>();
    }

    public void setAutoConnect(Boolean auto) {
        this.mAutoConnect = auto;
    }

    protected boolean init() {
        mBluetoothChat = new BluetoothChatService(mAutoConnect);
        mBluetoothChat.setListener(this);

        if (mPrtObject != null) {
            mPrtObject.init();
        }
        return true;
    }

    //TODO
    protected boolean init(Looper looper) {
        if (looper == null)
            return false;

        mLooper = looper;
        return this.init();
    }

    protected void uinit() {

        if (mBluetoothChat != null) {
            mBluetoothChat.stop();
            mBluetoothChat.setListener(null);
            mBluetoothChat = null;
        }

        if (mPrtObject != null) {
            mPrtObject.uinit();
            mPrtObject = null;
        }
    }

    static public BaseHealthService createServiceByBondDevice(BluetoothDevice device) {
        if (device == null) {
            return null;
        }

        BaseHealthService service = null;
        String UDID = device.getName();

        if (TextUtils.equals(UDID, PrtEtc_HC503B.UDID)) {
            service = new ETC_HC503Service();
        } else if (TextUtils.equals(UDID, PrtFt_BP_88A.UDID)) {//
//			if (FT_FinltopDiscovery.mFinltopDeviceMap.get(PrtFt_BP_88A.ID) == device.getAddress() ){
            service = new FT_BP_88AService();
//			}else if (FT_FinltopDiscovery.mFinltopDeviceMap.get(PrtFt_E100.ID) == device.getAddress()){
//				service = new FT_E100Service();
//			}
        }

        if (service == null) return null;

        service.setBondDevice(device);
        return service;
    }

    public Boolean connect() {
        if (mBluetoothChat == null) {
            Log.e(TAG, "has not setup bluetooth device !");
            return false;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.e(TAG, "setup BluetoothAdapter failed!");
            return false;
        }

        Set<BluetoothDevice> bondDevices = bluetoothAdapter.getBondedDevices();

        BluetoothDevice device = null;
        for (BluetoothDevice obj : bondDevices) {
            if (TextUtils.equals(obj.getAddress(), mBluetoothDevice.getAddress())) {
                device = obj;
                break;
            }
        }

        if (device == null) {
            Log.e(TAG, "cant find bondDevice !");
            return false;
        }

        mBluetoothChat.connect(device);

        return true;
    }

    public boolean disconnect() {
        try {
            this.uinit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getConnectStatus() {
        if (mBluetoothChat == null)
            return BluetoothChatService.EnumBlutToothState.STATE_NONE;

        synchronized (mBluetoothChat) {
            return mBluetoothChat.getState();
        }
    }

    private void setBondDevice(BluetoothDevice device) {
        mBluetoothDevice = device;
    }

    public String getAddress() {
        if (mBluetoothDevice != null) {
            return mBluetoothDevice.getAddress();
        }
        return "";
    }

    public void addListener(BaseHealthServiceListener listener) {
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    public void removeListener(BaseHealthServiceListener listener) {
        synchronized (mListeners) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }
    }

    public void removeAllListener() {
        synchronized (mListeners) {
            mListeners.clear();
        }
    }


    public void onConnecting(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        Log.i(TAG, "connecting to bluetoothDevice -" + mPrtObject.getmUUID());

        if (mLooper == null) {
            mLooper = Looper.getMainLooper();
        }

//		Looper.prepare();
        final ArrayList<BaseHealthServiceListener> listeners = mListeners;
        Handler h = new Handler(mLooper, new Callback() {
            public boolean handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null && (listener instanceof BaseHealthServiceListener) == true) {
                        listener.onConnecting(BaseHealthService.this);
                    }

                }
                return false;
            }
        });
        h.sendEmptyMessage(1);
//		Looper.loop();
    }

    public void onConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        Log.i(TAG, "connected to bluetoothDevice -" + mPrtObject.getmUUID());

        if (mLooper == null) {
            mLooper = Looper.getMainLooper();
        }
//		Looper.prepare();		
        final ArrayList<BaseHealthServiceListener> listeners = mListeners;
        Handler h = new Handler(mLooper, new Callback() {

            public boolean handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null && (listener instanceof BaseHealthServiceListener) == true) {
                        listener.onConnected(BaseHealthService.this);
                    }
                }
                return false;
            }
        });
        h.sendEmptyMessage(1);

//		Looper.loop();
    }

    public void onConnectFailed(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        Log.i(TAG, "connect failed to the bluetoothDevice -" + mPrtObject.getmUUID());

        if (mLooper == null) {
            mLooper = Looper.getMainLooper();
        }
//		Looper.prepare();
        final ArrayList<BaseHealthServiceListener> listeners = mListeners;
        Handler h = new Handler(mLooper, new Callback() {
            public boolean handleMessage(Message arg0) {
                // TODO Auto-generated method stub
                for (BaseHealthServiceListener listener : listeners) {
                    listener.onConnectFailed(BaseHealthService.this);
                }
                return false;
            }
        });
        h.sendEmptyMessage(1);
//		Looper.loop();
    }

    public void onDisConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        Log.i(TAG, "disconnect to the bluetoothDevice -" + mPrtObject.getmUUID());

        if (mLooper == null) {
            mLooper = Looper.getMainLooper();
        }
//		Looper.prepare();
        final ArrayList<BaseHealthServiceListener> listeners = mListeners;
        Handler h = new Handler(mLooper, new Callback() {
            public boolean handleMessage(Message arg0) {
                // TODO Auto-generated method stub
                for (BaseHealthServiceListener listener : listeners) {
                    listener.onDisConnected(BaseHealthService.this);
                }
                return false;
            }
        });
        h.sendEmptyMessage(1);
//		Looper.loop();
    }

    public void onListening(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Listening...");
    }

    public void onChatMsgRecv(BluetoothChatService blueToothChat, byte[] recv) {
        // TODO Auto-generated method stub
        String log = "";
        for (byte b : recv) {
            log += " " + b;
        }

        Log.e(TAG, "recv data count:" + recv.length + "recv data :" + log);

    }

    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub

        String log = "";
        for (byte b : send) {
            log += " " + b;
        }
        Log.e(TAG, "send data count:" + send.length + "send data :" + log);
    }

    public void onMasterErr() {
        // TODO Auto-generated method stub

    }

    public boolean onMasterSend(byte[] buf) {
        // TODO Auto-generated method stub
        mBluetoothChat.write(buf);
        return true;
    }
}

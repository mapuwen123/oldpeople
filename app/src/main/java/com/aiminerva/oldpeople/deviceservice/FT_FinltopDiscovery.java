package com.aiminerva.oldpeople.deviceservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.prt.PrtFinltopDiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class FT_FinltopDiscovery implements FT_FinltopService.FT_FinltopServiceListener {

    public static HashMap<Integer, String> mFinltopDeviceMap = new HashMap<Integer, String>();
    public static boolean mbDiscovery = false;
    private String TAG = "FT_FinltopService";
    private ArrayList<BaseHealthService> mFtFinltopServices;
    private FT_FinltopDiscoveryListener mListener;

    public interface FT_FinltopDiscoveryListener {
        public void onDiscoveryOver(FT_FinltopDiscovery service);
    }

    public FT_FinltopDiscovery() {
        mFtFinltopServices = new ArrayList<BaseHealthService>();
    }

    public boolean create() {
        synchronized (mFtFinltopServices) {

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter == null) {
                return false;
            }
            Log.e("setupChat", "setupChat 1!");
            Set<BluetoothDevice> bondDevices = bluetoothAdapter
                    .getBondedDevices();

            for (BaseHealthService service : mFtFinltopServices) {
                service.uinit();
                service.removeAllListener();
            }
            mFtFinltopServices.clear();

            for (BluetoothDevice obj : bondDevices) {
                String UDID = obj.getName();
                if (TextUtils.equals(UDID, PrtFinltopDiscovery.UDID)) {
                    BaseHealthService healthService = BaseHealthService
                            .createServiceByBondDevice(obj);
                    if (healthService == null) {
                        continue;
                    }

                    mFtFinltopServices.add(healthService);
                    Log.e("setupChat",
                            "setup bond BlueTooth Device == " + obj.getAddress());
                }
            }
        }
        return true;
    }

    public boolean startDiscovery() {
        BaseHealthService service = null;
        for (BaseHealthService it : mFtFinltopServices) {
            service = it;
            mFtFinltopServices.remove(it);
            break;
        }

        if (service != null) {
            service.connect();
            return true;
        }
        return false;
    }

    public void addListener(FT_FinltopDiscoveryListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    @Override
    public void onConnecting(BaseHealthService service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(BaseHealthService service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDisConnected(BaseHealthService service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectFailed(BaseHealthService service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinltopDiscovery(FT_FinltopService service, int type) {
        // TODO Auto-generated method stub
        mFinltopDeviceMap.put(type, service.getAddress());

        service.removeListener(this);
        service.uinit();

        if (mFtFinltopServices.size() == 0) {
            if (mListener != null) {
                mListener.onDiscoveryOver(this);
            }
            return;
        }

        BaseHealthService ser = null;
        for (BaseHealthService it : mFtFinltopServices) {
            ser = it;
            mFtFinltopServices.remove(it);
            break;
        }

        if (service != null) {
            service.connect();
        }
    }
}

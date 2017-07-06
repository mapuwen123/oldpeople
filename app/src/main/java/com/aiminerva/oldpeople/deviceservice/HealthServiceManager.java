package com.aiminerva.oldpeople.deviceservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;
//import android.app.Activity;

public class HealthServiceManager extends Object {
    private static final String TAG = "HealthServiceManager";

    private static HealthServiceManager mHealthServiceManager = null;
    private static BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BaseHealthService> mBlueToothServiceList;

    private HealthServiceManager() {
        // Exists only to defeat instantiation.
        mBlueToothServiceList = new ArrayList<BaseHealthService>();
    }

    public static HealthServiceManager getInstance() {
        if (mHealthServiceManager == null) {
            mHealthServiceManager = new HealthServiceManager();
        }
        return mHealthServiceManager;
    }

    // public method - init
    public Boolean setupChat(Boolean autorun) {

        Log.e("setupChat", "setupChat !");

        synchronized (mBlueToothServiceList) {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                return false;
            }
            Log.e("setupChat", "setupChat 1!");
            Set<BluetoothDevice> bondDevices = mBluetoothAdapter
                    .getBondedDevices();

            for (BaseHealthService service : mBlueToothServiceList) {
                service.uinit();
                service.removeAllListener();
            }
            mBlueToothServiceList.clear();

            Log.e("setupChat", "setup bond BlueTooth Device begin !");

            for (BluetoothDevice obj : bondDevices) {

                BaseHealthService healthService = BaseHealthService
                        .createServiceByBondDevice(obj);
                if (healthService == null) {
                    continue;
                }

                healthService.setAutoConnect(autorun);

                mBlueToothServiceList.add(healthService);
                Log.e("setupChat",
                        "setup bond BlueTooth Device == " + obj.getAddress());
            }

            // Initialize the buffer for outgoing messages

            Log.e("setupChat", "setup bond BlueTooth Device finish !");

            // autoConnectAllDevices();
        }

        return true;
    }

    /**
     * public method - get service list
     *
     * @return
     */
    public ArrayList<BaseHealthService> getServiceList() {
        synchronized (mBlueToothServiceList) {
            return mBlueToothServiceList;
        }
    }

    /**
     * public method - discovery BlueToothDevice
     */
    public void discovery() {

    }

    public void autoConnectAllDevices() {
        synchronized (mBlueToothServiceList) {

            for (BaseHealthService service : mBlueToothServiceList) {
                connect(service);
            }
        }
    }

    /**
     * public method - connect to address's BlueToothDevice
     *
     * @param device
     */
    public void connect(BaseHealthService device) {
        // TODO Auto-generated method stub
        if (device != null) {
            device.init();
            device.connect();
            return;
        }
    }

    /**
     * public method - disconnect to address's BlueToothDevice
     **/
    public void diconnect(BaseHealthService device) {
        if (device != null) {
            device.uinit();
        }
    }

    public void uinit() {
        synchronized (mBlueToothServiceList) {
            if (mBlueToothServiceList == null) {
                return;
            }

            for (BaseHealthService service : mBlueToothServiceList) {
                service.uinit();
                service.removeAllListener();
            }
            mBlueToothServiceList.clear();
        }
    }

    /**
     * public method - bluetooth adaptor is enabled
     *
     * @return
     */
    public boolean isBluetoothEnable() {

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                return false;
            }
        }

        synchronized (mBluetoothAdapter) {
            return mBluetoothAdapter.isEnabled();
        }
    }

    /**
     * public method - bluetooth adaptor valid or invalid
     *
     * @return
     */
    public boolean isBluetoothValid() {

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                return false;
            }
        }

        synchronized (mBluetoothAdapter) {

            return true;
        }
    }

    /**
     * public method - enable bluetooth
     *
     * @param on : true enable , false disable
     * @return
     */
    public boolean enableBluetooth(boolean on) {

        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                return false;
            }
        }

        synchronized (mBluetoothAdapter) {

            if (on) {
                mBluetoothAdapter.enable();
            } else {
                mBluetoothAdapter.disable();
            }
            return true;
        }

    }
}

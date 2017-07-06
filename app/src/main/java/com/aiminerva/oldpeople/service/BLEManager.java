package com.aiminerva.oldpeople.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BLEManager {
	
	private static final String TAG = "BLEManager";
    private BluetoothAdapter   mBluetoothAdapter;
    private BluetoothDevice    mTargetDevice;
    private BluetoothLeService mBluetoothLeService;
    public static BLEHelper mBleHelper;  
    private Context mContext;
    private boolean bScanning;
    private Handler mHandler;
    
    private static final long SCAN_PERIOD = 15000;
    public static final String ACTION_FIND_DEVICE = "find_device";
    public static final String ACTION_SEARCH_TIME_OUT = "search_timeout";
    public static final String ACTION_START_SCAN = "start_scan";
    
	public BLEManager(Context context,BluetoothAdapter adapter) {
		mContext = context;
		mHandler = new Handler();
		mBluetoothAdapter = adapter;
	}
	
	public void scanLeDevice(final boolean enable) {
		if (enable) {
			bScanning = true;
			broadcastUpdate(ACTION_START_SCAN);
			mBluetoothAdapter.startLeScan(mLeScanCallback);	
		} else {
			bScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

//	BroadcastReceiver receiver = new BroadcastReceiver() {     
//		   @Override     
//		  public void onReceive(Context context, Intent intent) {     
//		       String action = intent.getAction();     
//		       System.out.println("aaa: " + action);
//		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {     
//		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);     
//		            System.out.println(device.getName());     
//		       }     
//		   }     
//		}  ;
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			switch(BluetoothLeService.MODE){
	    	case BluetoothLeService.MODE_XUETANG://血糖
	    		if("JKBGM".equalsIgnoreCase(device.getName())){
					mTargetDevice = device;
					scanLeDevice(false);				
					//Log.d(TAG, "find-->"+mTargetDevice.getName());
					broadcastUpdate(ACTION_FIND_DEVICE);
					BluetoothLeService.setMode(BluetoothLeService.MODE_XUETANG);//New Add
					
					// start BluetoothLeService
					Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
					mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);
					
				}
	    		break;
	    	case BluetoothLeService.MODE_XUEYANG://血氧
	    		if ("PC-60NW-1".equalsIgnoreCase(device.getName())||
	    				"POD".equalsIgnoreCase(device.getName())||
	    				"PC-68B".equalsIgnoreCase(device.getName())) {
	    				mTargetDevice = device;
	    				scanLeDevice(false);				
	    				//Log.d(TAG, "find-->"+mTargetDevice.getName());
	    				broadcastUpdate(ACTION_FIND_DEVICE);
	    				BluetoothLeService.setMode(BluetoothLeService.MODE_XUEYANG);//New Add
	    				
	    				// start BluetoothLeService
	    				Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
	    				mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);
	    			}
	    		break;
	    	case BluetoothLeService.MODE_XUEYA://血压    
	    		if("BPM-188".equalsIgnoreCase(device.getName())){
					mTargetDevice = device;
					scanLeDevice(false);				
					//Log.d(TAG, "find-->"+mTargetDevice.getName());
					broadcastUpdate(ACTION_FIND_DEVICE);
					BluetoothLeService.setMode(BluetoothLeService.MODE_XUEYA);//New Add
					
					// start BluetoothLeService
					Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
					mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);
					
				}
	    		break;
	    	case BluetoothLeService.MODE_XINDIAN://心电
	    		if("PC80B".equalsIgnoreCase(device.getName())){
					mTargetDevice = device;
					scanLeDevice(false);				
					//Log.d(TAG, "find-->"+mTargetDevice.getName());
					broadcastUpdate(ACTION_FIND_DEVICE);
					BluetoothLeService.setMode(BluetoothLeService.MODE_XINDIAN);//New Add
					
					// start BluetoothLeService
					Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
					mContext.bindService(gattServiceIntent, mServiceConnection, mContext.BIND_AUTO_CREATE);
					
				}
	    		break;
	    	
			}
		}
	};
	
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");		
				//Toast.makeText(mContext, "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
				return;
			}
			
			mBleHelper = new BLEHelper(mBluetoothLeService);
			
			// Automatically connects to the device upon successful start-up
			// initialization.
			mBluetoothLeService.connect(mTargetDevice.getAddress());
//			mBluetoothLeService.connect(mTargetDevice);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			mBleHelper = null;
		}
	};
	
	
	public void closeService(Context context){
		if (mTargetDevice != null) {
			context.unbindService(mServiceConnection);
			mBluetoothLeService.close();
			mBluetoothLeService = null;
			Log.d(TAG, "-- closeService --");
		}
	}
	
	/**
	 * 断开连接
	 */
	public void disconnect() {
		scanLeDevice(false);
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
		}
	}
	public void reConnect(){
		if (mBluetoothLeService != null) {
			mBluetoothLeService.disconnect();
		}
		scanLeDevice(true);
		
	}
		
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }
	
	/**
	 * 自定义过滤器
	 * custom intentFilter
	 */
	public static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(BluetoothLeService.ACTION_BLUETOOTH_RESULT);
		
		
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		//---
		intentFilter.addAction(BluetoothLeService.ACTION_SPO2_DATA_AVAILABLE);
		intentFilter.addAction(BluetoothLeService.ACTION_CHARACTER_NOTIFICATION);
		intentFilter.addAction(ACTION_FIND_DEVICE);
		intentFilter.addAction(ACTION_SEARCH_TIME_OUT);
		intentFilter.addAction(ACTION_START_SCAN);
		return intentFilter;
	}

}

package com.aiminerva.oldpeople.libdemo;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.aiminerva.oldpeople.bluetooth4.ble.service.BLEManager;
import com.aiminerva.oldpeople.bluetooth4.ble.service.ReaderBLE;
import com.aiminerva.oldpeople.bluetooth4.ble.service.SenderBLE;
import com.creative.FingerOximeter.FingerOximeter;
import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.BaseDate;

import java.util.List;

/**
 * POD,PC-60NW-1,PC-68B.
 * 
 * use BluetoothLeService.
 * you can modefy bluetooth by your need
 */
public class Bluetooth4Manager/* extends Activity*/{

	private static final String TAG = "MainActivity";
	public FingerOximeter mFingerOximeter;
	public boolean isUpdate;
	
	public BLEManager mManager;
		
	
	Activity mActivity;
	public Bluetooth4Manager(Activity mActivity){
		this.mActivity = mActivity;
        initBLE();	             
        android6_RequestLocation(mActivity);
	}

	

	
	/**
	 * 收到的血氧仪数据
	 * received FingerOximeter of data
	 */
	class FingerOximeterCallBack implements IFingerOximeterCallBack {

		@Override
		public void OnGetSpO2Param(int nSpO2, int nPR, float fPI, boolean nStatus, int nMode, float nPower) {			
//			Message msg = myHandler.obtainMessage(MSG_DATA_SPO2_PARA);
			Bundle data = new Bundle();
			data.putInt("nSpO2", nSpO2);
			data.putInt("nPR", nPR);
			data.putFloat("fPI", fPI);
			data.putFloat("nPower", nPower);
			data.putBoolean("nStatus", nStatus); 
			data.putInt("nMode", nMode);
			data.putFloat("nPower", nPower);
//			msg.setData(data);
//			myHandler.sendMessage(msg);	
			
			//myHandler.obtainMessage(2, "数据--" + nSpO2 + " " + nPR + " " + nPI).sendToTarget();
		}

		//血氧波形数据采样频率：50Hz，每包发送 5 个波形数据，即每 1 秒发送 10 包波形数据
		//参数 waves 对应一包数据
		//spo2 sampling rate is 50hz, 5 wave data in a packet, 
		//send 10 packet 1/s. param "waves" is 1 data packet
		@Override
		public void OnGetSpO2Wave(List<BaseDate.Wave> waves) {
			//Log.d(TAG, "wave.size:"+waves.size()); // size = 5
//			SPO_RECT.addAll(waves);	
//			SPO_WAVE.addAll(waves);
			
			//myHandler.obtainMessage(MSG_DATA_SPO2_WAVE, waves).sendToTarget();						
		}

		@Override
		public void OnGetDeviceVer(int nHWMajor, int nHWMinor, int nSWMajor, int nSWMinor) {
//			myHandler.obtainMessage(MSG_BLUETOOTH_STATE, "device info,获取到设备信息:" + nHWMajor).sendToTarget();
		}

		@Override
		public void OnConnectLose() {
//			myHandler.obtainMessage(MSG_BLUETOOTH_STATE, "connect lost,连接丟失").sendToTarget();
		}
	}
	
	
	//-----------------  ble operation ---------------------------
    private BluetoothAdapter mBluetoothAdapter;
    
	private void initBLE(){
        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mActivity, "BLE is not supported", Toast.LENGTH_SHORT).show();
        }
		
        final BluetoothManager bluetoothManager =
        		(BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(mActivity, "error bluetooth not supported", Toast.LENGTH_SHORT).show();           
        }else{
        	mBluetoothAdapter.enable();       	
        	mManager = new BLEManager(mActivity, mBluetoothAdapter);       	
        }                     
	}

	public void startFingerOximeter(){	
		if(BLEManager.mBleHelper!=null){
			mFingerOximeter = new FingerOximeter(new ReaderBLE(BLEManager.mBleHelper), new SenderBLE(BLEManager.mBleHelper), new FingerOximeterCallBack());
			mFingerOximeter.Start();
//			mFingerOximeter.SetWaveAction(true);	
//			startDraw();
		}
	}

	/**
	 * android6.0 Bluetooth, need to open location for bluetooth scanning
	 * android6.0 蓝牙扫描需要打开位置信息
	 */
	private void android6_RequestLocation(final Context context){
		if (Build.VERSION.SDK_INT >= 23) {
			// BLE device need to open location
	        if (mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
	        		&& !isGpsEnable(context)) {	                     
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setCancelable(false);
				builder.setTitle("Prompt")
						.setIcon(android.R.drawable.ic_menu_info_details)
						.setMessage("Android6.0 need to open location for bluetooth scanning")
						.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {								
								Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								// startActivityForResult(intent,0);
								context.startActivity(intent);
							}
						});  
				builder.show();
	        }
			
	        //request permissions
			int checkCallPhonePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
			if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
				//判断是否需要 向用户解释，为什么要申请该权限
				if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION))
					Toast.makeText(context,"need to open location info for discovery bluetooth device in android6.0 version，otherwise find not！", Toast.LENGTH_LONG).show();
				//请求权限
				ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
			}
		}	
		
	}
	
	// whether or not location is open, 位置是否打开
	public final boolean isGpsEnable(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}			
		return false;
	}
}

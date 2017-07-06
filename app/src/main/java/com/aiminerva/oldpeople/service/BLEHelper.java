package com.aiminerva.oldpeople.service;

import android.bluetooth.BluetoothGattCharacteristic;

import java.io.IOException;

public class BLEHelper{
	
	private BluetoothLeService mBluetoothLeService;
	//private static final String TAG ="BLEHelper";	
	
	
	public BLEHelper(BluetoothLeService bluetoothLeService){
		mBluetoothLeService = bluetoothLeService;
	}	
	
    public void write(byte[] bytes) {
    	synchronized (this) {
        	if(mBluetoothLeService!=null){
            	BluetoothGattCharacteristic chara = mBluetoothLeService.getGattCharacteristic(
            			BluetoothLeService.UUID_CHARACTER_WRITE);
            	if(chara !=null){
            		mBluetoothLeService.write(chara, bytes);
            	}       	
        	}
		}
	}
    
    public int read(byte[] bytes) throws IOException{
    	int temp=0;
    	if(mBluetoothLeService!=null){
    		temp= mBluetoothLeService.read(bytes);
    	}
    	return temp;
    }
    

	public void clean() {
    	if(mBluetoothLeService!=null){
    		mBluetoothLeService.clean();
    	}
	}

	public int available() throws IOException {
    	if(mBluetoothLeService!=null){
    		return mBluetoothLeService.available();
    	}
    	return 0;
	}
	
    
}

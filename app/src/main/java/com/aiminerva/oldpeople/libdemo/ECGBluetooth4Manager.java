//package com.aiminerva.oldpeople.libdemo;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothManager;
//import android.bluetooth.BluetoothSocket;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.widget.Toast;
//
//import com.aiminerva.oldpeople.bean.DeviceInfo;
//import com.aiminerva.oldpeople.bean.Poinots;
//import com.aiminerva.oldpeople.deviceservice.HealthServiceManager;
//import com.aiminerva.oldpeople.globalsettings.GlobalConstants;
//import com.aiminerva.oldpeople.utils.L;
//import com.creative.base.BaseDate;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//import java.util.Vector;
//
///**
// * Created by Aaron on 2017/2/14.
// */
//
//public class ECGBluetooth4Manager {
//    private ECGActivity ecgActivity;
//    public ECGBluetooth4Manager(ECGActivity ecgActivity){
//        this.ecgActivity = ecgActivity;
//        initBLE();
//        android6_RequestLocation(ecgActivity);
//    }
//    public void scanLeDevice(boolean enable){
//        if(enable){
//            ecgActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    L.i("开始连接");
////                    ecgActivity.ecgPager.changeState(GlobalConstants.START_CONNECT);
//                }
//            });
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            adapter.startDiscovery();
//        }else{
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            adapter.cancelDiscovery();
//        }
//
//    }
//    private BluetoothAdapter   mBluetoothAdapter;
//
//    private void initBLE(){
//        if (!ecgActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(ecgActivity, "BLE is not supported", Toast.LENGTH_SHORT).show();
//        }
//
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) ecgActivity.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//        // 检查设备上是否支持蓝牙
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(ecgActivity, "error bluetooth not supported", Toast.LENGTH_SHORT).show();
//        }else{
//            mBluetoothAdapter.enable();
////            mManager = new BLEManager(ecgActivity, mBluetoothAdapter);
//        }
//    }
//    private void android6_RequestLocation(final Context context){
//        if (Build.VERSION.SDK_INT >= 23) {
//            // BLE device need to open location
//            if (ecgActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
//                    && !isGpsEnable(context)) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setCancelable(false);
//                builder.setTitle("Prompt")
//                        .setIcon(android.R.drawable.ic_menu_info_details)
//                        .setMessage("Android6.0 need to open location for bluetooth scanning")
//                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        // startActivityForResult(intent,0);
//                        context.startActivity(intent);
//                    }
//                });
//                builder.show();
//            }
//
//            //request permissions
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(ecgActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                //判断是否需要 向用户解释，为什么要申请该权限
//                if (ActivityCompat.shouldShowRequestPermissionRationale(ecgActivity, Manifest.permission.ACCESS_COARSE_LOCATION))
//                    Toast.makeText(context,"need to open location info for discovery bluetooth device in android6.0 version，otherwise find not！", Toast.LENGTH_LONG).show();
//                //请求权限
//                ActivityCompat.requestPermissions(ecgActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
//            }
//        }
//
//    }
//    // whether or not location is open, 位置是否打开
//    public final boolean isGpsEnable(final Context context) {
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps || network) {
//            return true;
//        }
//        return false;
//    }
//
//
//    public BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//
//                // 获取查找到的蓝牙设备
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if("ECG:HC-201B".equalsIgnoreCase(device.getName())){
//                    scanLeDevice(false);
//                    int connectState = device.getBondState();
//                    if(connectState == BluetoothDevice.BOND_BONDED){
//                        ecgActivity.switchHealthDevice(DeviceInfo.DEVICE_TYPE_EGC);
//                    }
//                }
//                else if("PC80B".equalsIgnoreCase(device.getName())){
//                    scanLeDevice(false);
//                    // 获取蓝牙设备的连接状态
//                    int connectState = device.getBondState();
//                    switch (connectState) {
//                        // 未配对
//                        case BluetoothDevice.BOND_NONE:
//                            // 配对
//                            try {
//                                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
//                                createBondMethod.invoke(device);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            break;
//                        // 已配对
//                        case BluetoothDevice.BOND_BONDED:
//                            // 连接
//                            connect(device);
//                            break;
//                    }
//                }
//            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                // 状态改变的广播
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if("PC80B".equalsIgnoreCase(device.getName())){
//                    int connectState = device.getBondState();
//                    switch (connectState) {
//                        case BluetoothDevice.BOND_NONE:
//                            break;
//                        case BluetoothDevice.BOND_BONDING:
//                            break;
//                        case BluetoothDevice.BOND_BONDED:
//                            // 连接
//                            connect(device);
//                            break;
//                    }
//                }
//            }
//        }
//    };
//
//
//    UpdateData updateData = new UpdateData();
//    AnalyseData analyseData = new AnalyseData(new IECGCallBack() {
//        public void OnGetDeviceVer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
//        }
//        public void OnGetRequest(String paramString1, String paramString2, int paramInt1, int paramInt2) {
//        }
//        public void OnGetFileTransmit(int paramInt, Vector<Integer> paramVector) {
//        }
//        public void OnGetRealTimePrepare(boolean paramBoolean, BaseDate.ECGData paramECGData, int paramInt) {
////            for(int i = 0 ; i < paramECGData.data.size(); i ++){
////                updateData.addData(paramECGData.data.get(i).data);
////            }
//        }
//        public void OnGetRealTimeMeasure(boolean paramBoolean, BaseDate.ECGData paramECGData, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
//            for(int i = 0 ; i < paramECGData.data.size(); i ++){
//                updateData.addData(paramECGData.data.get(i).data);
//            }
//        }
//        public void OnGetRealTimeResult(String paramString, int paramInt1, int paramInt2, int paramInt3) {
//            updateData.hr = (byte) paramInt3;
//            updateData.result = (byte)paramInt2;
//            updateData.updateToService();
//        }
//        public void OnGetPower(int paramInt) {
//        }
//        public void OnReceiveTimeOut(int paramInt) {
//        }
//        public void OnConnectLose() {
//        }
//    });
//
//    private void connect(BluetoothDevice device) {
//        try {
//            final BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//            socket.connect();
//            ecgActivity.ecgDeviceName = "PC80B";
////            ecgActivity.ecgPager.changeState(GlobalConstants.CONNECTED);
//            L.i("已连接");
//            if(ecgActivity.service!=null) HealthServiceManager.getInstance().diconnect(ecgActivity.service);
//            final InputStream is = socket.getInputStream();
//            new Thread() {
//                private byte[] buffer = new byte[''];
//                public void run() {
//                    try {
//                        while(true){
//                            int len1 = is.read(this.buffer);
//                            Receive.originalData.clear();
//                            for (int i = 0; i < len1; i++) {
//                                Receive.originalData.add(Byte.valueOf(this.buffer[i]));
//                            }
//                            analyseData.analyse();
//                        }
//                    } catch (IOException e) {
//						scanLeDevice(true);
//                        System.out.println(e.getMessage());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (socket != null) {
//                        try {
//                            socket.close();
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            }.start();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//
//    class UpdateData{
//        byte hr;
//        byte result;
//        Vector<Byte> data = new Vector();//(Byte.valueOf(this.buffer[i]));
//        public void addData(int arg){
//            byte[] uploadCount = short2byte(arg);
//            data.add(uploadCount[0]);
//            data.add(uploadCount[1]);
//        }
//        public void updateToService(){
//            int nLength = data.size()/2;
//            data.add(0,hr);
//            data.add(1,result);
//            System.out.println(nLength);
//            byte[] uploadCount = short2byte(nLength);
//            data.add(2,uploadCount[0]);
//            data.add(3,uploadCount[1]);
//            byte[] bytes = new byte[nLength*2+4];
//            for(int i = 0 ; i < bytes.length;i++){
//                bytes[i] = data.get(i);
//            }
//
//            List<Poinots> data = new ArrayList<>();
//            for (int i = 0; i < bytes.length; i ++) {
//                Poinots poinots = new Poinots();
//                poinots.i = i;
//                poinots.value = bytes[i];
//                data.add(poinots);
//            }
//            ecgActivity.onUpdateECGFromList(data);
//
//            //将byte数据保存为文件并返回
////            final String filePath = writeToFile(bytes);
////            if(filePath != null){
////                ecgActivity.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        ecgActivity.onUpdateECG(filePath);
////                    }
////                });
////            }
//        }
//        public  byte[] short2byte(int res) {
//            byte[] targets = new byte[2];
//            targets[0] = (byte) (res & 0xff);// 最低位
//            targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
//            return targets;
//        }
//        public String writeToFile(byte[] data) {
//            long time = System.currentTimeMillis();
//            SimpleDateFormat format = new SimpleDateFormat("yyMMddHH.mms");// 10012710.17
//            String mFileName = format.format(new Date(time));
//            String path = null;
//            try {
//
//                if (!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
//                    FileOutputStream stream = ecgActivity.openFileOutput(mFileName,Context.MODE_PRIVATE);
//                    byte[] buf = data;
//                    stream.write(buf);
//                    stream.close();
//                    File fileDir = ecgActivity.getFilesDir();
//                    path = fileDir.getAbsolutePath();
//                    return  path;
//                }
//
//                String cacheDir = GlobalConstants.FILE_HEALTH_DIR.getAbsolutePath();
//                File saveFile = new File(cacheDir, mFileName);
//                FileOutputStream outStream = new FileOutputStream(saveFile);
//                outStream.write(data);
//                outStream.close();
//                path = saveFile.getAbsolutePath();
//
//
//                FileInputStream inputStream = new FileInputStream(new File(path));
//                byte[] readData = new byte[8];
//                inputStream.read(readData);
//                System.out.println("-----------------------------------");
//                System.out.println("aaaaa  " + (readData[0] & 0xFF));
//                System.out.println("aaaaa  " + (readData[1] & 0xFF));
//                System.out.println("aaaaa  " + (((readData[3] & 0xFF)<<8)|(readData[2] & 0xFF)));
//                System.out.println("aaaaa  " + (((readData[5] & 0xFF)<<8)|(readData[4] & 0xFF)));
//                System.out.println("aaaaa  " + (((readData[7] & 0xFF)<<8)|(readData[6] & 0xFF)));
//                System.out.println("-----------------------------------");
//
//                inputStream.close();
//                return path;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return path;
//        }
//    }
//}

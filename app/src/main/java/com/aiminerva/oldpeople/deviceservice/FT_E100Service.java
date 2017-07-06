package com.aiminerva.oldpeople.deviceservice;

import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC201B;
import com.aiminerva.oldpeople.deviceservice.prt.PrtFt_E100;

public class FT_E100Service extends BaseHealthService implements PrtFt_E100.PrtFt_E100Listener {

    // --reponse data
    public PrtEtc_HC201B.PrtECGModel repECGModel;
    private String TAG = "ETC_HC201Service";

    public interface FT_E100ServiceListener extends BaseHealthServiceListener {
        void onUpdateECG(FT_E100Service sevice);

        void onUpdateError(FT_E100Service sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        super.mPrtObject = new PrtFt_E100();
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

        PrtFt_E100 prtObject = (PrtFt_E100) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

        PrtFt_E100 prtObject = (PrtFt_E100) super.mPrtObject;
        prtObject.masterSnd(send);
    }

    public boolean onMasterReceive(byte command, PrtFt_E100.PrtFtE100Model result) {
        // TODO Auto-generated method stub
//		UploadECGFileRequest request = new UploadECGFileRequest(this);
//		request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;
//		request.reqECGFile = result.mFilePath;
//		Log.e("ecg","uid = " + GlobalSettings.getInstance().mCurrentRelationMember);
//		Log.e("ecg","mFilePath = " + result.mFilePath);
//
//		RequestManager.getInstance().sendRequest(request);
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

//	public void onECGFileUpdated(UploadECGFileRequest request) {
//		// TODO Auto-generated method stub
//
//		Log.i(TAG, "sendRequest response!");
//		Log.e(TAG, "上传心电文件成功！");
//
//		final List<BaseHealthServiceListener> listeners = super.mListeners;
//
//		if ( listeners.size() <=0 ){
//			return;
//		}
//
//		Handler h = new Handler(Looper.getMainLooper()){
//
//			public void handleMessage(Message arg0) {
//				// TODO Auto-generated method stub
//
//				Log.d(TAG, "listeners "+ listeners.size());
//				for (BaseHealthServiceListener listener : listeners) {
//					if (listener != null
//							&& (listener instanceof FT_E100ServiceListener) == true) {
//						FT_E100ServiceListener etcListener = (FT_E100ServiceListener) listener;
//						etcListener.onUpdateECG(FT_E100Service.this);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//
//	}
//
//	public void onRequestError(RequestBase request) {
//		// TODO Auto-generated method stub
//		Log.e(TAG, "上传心电文件失败！error"+ request.errorString);
//
//		final List<BaseHealthServiceListener> listeners = super.mListeners;
//
//		if ( listeners.size() <=0 ){
//			return;
//		}
//
//		final String errorString = request.errorString;
//
//		Handler h = new Handler(Looper.getMainLooper()){
//
//			public void handleMessage(Message arg0) {
//				// TODO Auto-generated method stub
//
//				Log.d(TAG, "listeners "+ listeners.size());
//				for (BaseHealthServiceListener listener : listeners) {
//					if (listener != null
//							&& (listener instanceof FT_E100ServiceListener) == true) {
//						FT_E100ServiceListener etcListener = (FT_E100ServiceListener) listener;
//						etcListener.onUpdateError(FT_E100Service.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}
}

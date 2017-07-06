package com.aiminerva.oldpeople.deviceservice;

import android.util.Log;

import com.aiminerva.oldpeople.bean.BloodPressInfo;
import com.aiminerva.oldpeople.deviceservice.prt.PrtMdl_BT20_203B;
import com.aiminerva.oldpeople.deviceservice.prt.PrtSino_WL1;

public class MDL_BT20_203BService extends BaseHealthService implements PrtMdl_BT20_203B.PrtMdl_BT20_203BListener {

    // --reponse data
    public PrtMdl_BT20_203B.PrtMDLBloodPressModel repBloodSugerModel;
    private String TAG = "MDL_BT20_203BService";

    public interface MDL_BT20_203BServiceListener extends BaseHealthServiceListener {
        void onUpdateBloodPress(MDL_BT20_203BService sevice);

        void onUpdateError(MDL_BT20_203BService sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtMdl_BT20_203B();
        super.mPrtObject.setListener(this);
        super.DEVICE_NAME = "血压仪";

        Log.v(TAG, "init success!");
        return true;
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

        PrtMdl_BT20_203B prtObject = (PrtMdl_BT20_203B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

        PrtSino_WL1 prtObject = (PrtSino_WL1) super.mPrtObject;
        prtObject.masterSnd(send);
    }

    @Override
    public boolean onMasterReceive(byte command, PrtMdl_BT20_203B.PrtMDLBloodPressModel result) {
        // TODO Auto-generated method stub

        BloodPressInfo bloodPress = new BloodPressInfo();
        bloodPress.mDiastolicPressure = result.mDiastolicPressure;
        bloodPress.mSystolicPressure = result.mSystolicPressure;
        bloodPress.mPluse = result.mPluse;
        bloodPress.mUDID = PrtMdl_BT20_203B.UDID;

//		UploadHealthInfoRequest request = new UploadHealthInfoRequest(this);
//		request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;
//		request.reqOrgId = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityid;
//		request.reqOrgName = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityname;
//		request.reqType = HealthIndicesInfo.HEALTH_TYPE_BLOODSUGER;
//		request.reqHealthInfo = bloodPress;
//		request.reqSession = "";
//
//		RequestManager.getInstance().sendRequest(request);
        Log.i(TAG, "sendRequest!");

        return true;
    }

//	public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
//		// TODO Auto-generated method stub
//
//		Log.i(TAG, "sendRequest response!");
//		Log.e(TAG, "上传血压数据成功！");
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
//							&& (listener instanceof MDL_BT20_203BServiceListener) == true) {
//						MDL_BT20_203BServiceListener etcListener = (MDL_BT20_203BServiceListener) listener;
//						etcListener.onUpdateBloodPress(MDL_BT20_203BService.this);
//					}
//
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}
//
//	public void onRequestError(RequestBase request) {
//		// TODO Auto-generated method stub
//		Log.e(TAG, "上传血压数据失败！error"+ request.errorString);
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
//							&& (listener instanceof PrtSino_WL1Listener) == true) {
//						MDL_BT20_203BServiceListener etcListener = (MDL_BT20_203BServiceListener) listener;
//						etcListener.onUpdateError(MDL_BT20_203BService.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}


}

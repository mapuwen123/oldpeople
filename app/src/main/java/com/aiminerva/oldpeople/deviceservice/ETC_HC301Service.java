package com.aiminerva.oldpeople.deviceservice;

import android.util.Log;

import com.aiminerva.oldpeople.bean.BodyFatInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC301B;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;

public class ETC_HC301Service extends BaseHealthService implements PrtEtc_HC301B.PrtEtc_HC301BListener {

    // --reponse data
    public PrtEtc_HC301B.PrtBodyfatModel repBodyfatModel;
    private String TAG = "ETC_HC301Service";

    public interface ETC_HC301ServiceListener extends BaseHealthServiceListener {
        void onUpdateBodyfat(ETC_HC301Service sevice);

        void onUpdateError(ETC_HC301Service sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub

        super.mPrtObject = new PrtEtc_HC301B();
        super.mPrtObject.setListener(this);

        super.DEVICE_NAME = "体质仪";
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

        PrtEtc_HC301B prtObject = (PrtEtc_HC301B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

//		PrtEtc_HC301B prtObject = (PrtEtc_HC301B) super.mPrtObject;
//		prtObject.masterSend(send);
    }

    public boolean onMasterReceive(byte command, PrtEtc_HC301B.PrtBodyfatModel result) {
        // TODO Auto-generated method stub
//		UploadHealthInfoRequest request = new UploadHealthInfoRequest(this);
//		request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;

        BodyFatInfo bodyFatInfo = new BodyFatInfo();
        bodyFatInfo.mBasalMetabolism = result.mBasalMetabolism;
        bodyFatInfo.mBMILevel = result.mBMILevel;
        bodyFatInfo.mBMIValue = result.mBMIValue;
        bodyFatInfo.mFatPercent = result.mFatPercent;
        bodyFatInfo.mHabitusLevel = result.mHabitusLevel;
        bodyFatInfo.mHeight = result.mHeight;
        bodyFatInfo.mSex = result.mSex;
        bodyFatInfo.mWeight = result.mWeight;
        bodyFatInfo.mYear = result.mYear;

        bodyFatInfo.mDate = System.currentTimeMillis();
        bodyFatInfo.mUDID = PrtEtc_HC301B.UDID;
        bodyFatInfo.mUserId = GlobalSettings.getInstance().mCurrentRelationMember;
        bodyFatInfo.mValueType = HealthIndicesInfo.HEALTH_TYPE_BODYFAT;

//		request.reqType = HealthIndicesInfo.HEALTH_TYPE_BODYFAT;
//		request.reqOrgId = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityid;
//		request.reqOrgName = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityname;
//		request.reqHealthInfo = bodyFatInfo;
//		request.reqSession = "";
//		RequestManager.getInstance().sendRequest(request);

        return true;
    }

//	public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
//		// TODO Auto-generated method stub
//
//		Log.i(TAG, "sendRequest response!");
//		Log.e(TAG, "上传体质数据成功！");
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
//							&& (listener instanceof ETC_HC301ServiceListener) == true) {
//						ETC_HC301ServiceListener etcListener = (ETC_HC301ServiceListener) listener;
//						etcListener.onUpdateBodyfat(ETC_HC301Service.this);
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
//		Log.e(TAG, "上传件失败！error"+ request.errorString);
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
//							&& (listener instanceof ETC_HC301ServiceListener) == true) {
//						ETC_HC301ServiceListener etcListener = (ETC_HC301ServiceListener) listener;
//						etcListener.onUpdateError(ETC_HC301Service.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}

}

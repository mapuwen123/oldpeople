package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.bean.BloodSugerInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.deviceservice.prt.PrtSino_WL1;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;

import java.util.List;

public class Sino_WL1Service extends BaseHealthService implements PrtSino_WL1.PrtSino_WL1Listener {

    // --reponse data
    public PrtSino_WL1.PrtSinoOxgenMode repBloodSugerModel;
    private String TAG = "Sino_WL1Service";

    private final static int MEASURE_STEP_START = 0;
    private final static int MEASURE_STEP_END = 1;
    private final static int MEASURE_STEP_NONE = 2;

    private int mMeasureStep = MEASURE_STEP_NONE;

    public interface Sino_WL1ServiceListener extends BaseHealthServiceListener {
        void onStartMeature(Sino_WL1Service sevice);

        void onUpdateBloodSuger(Sino_WL1Service sevice);

        void onUpdateError(Sino_WL1Service sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtSino_WL1();
        super.mPrtObject.setListener(this);
        super.DEVICE_NAME = "血糖仪";

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

        PrtSino_WL1 prtObject = (PrtSino_WL1) super.mPrtObject;
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
    public boolean onMasterReceive(byte command, PrtSino_WL1.PrtSinoOxgenMode result) {
        // TODO Auto-generated method stub
        if (PrtSino_WL1.COMMAND_TYPE_START == result.mStep) {
            mMeasureStep = MEASURE_STEP_START;

            final List<BaseHealthServiceListener> listeners = super.mListeners;

            if (listeners.size() <= 0) {
                return true;
            }

            Handler h = new Handler(Looper.getMainLooper()) {

                public void handleMessage(Message arg0) {
                    // TODO Auto-generated method stub

                    Log.d(TAG, "listeners " + listeners.size());
                    for (BaseHealthServiceListener listener : listeners) {
                        if (listener != null
                                && (listener instanceof Sino_WL1ServiceListener) == true) {
                            Sino_WL1ServiceListener etcListener = (Sino_WL1ServiceListener) listener;
                            etcListener.onStartMeature(Sino_WL1Service.this);
                        }

                    }
                }

            };
            h.sendEmptyMessage(1);

        } else if (PrtSino_WL1.COMMAND_TYPE_LIVEDATA == result.mStep) {

            //if ( MEASURE_STEP_START == mMeasureStep ){
            BloodSugerInfo bloodSuger = new BloodSugerInfo();
            bloodSuger.mBloodSuger = result.mBloodSuger;
            bloodSuger.mUDID = PrtSino_WL1.UDID;
            bloodSuger.mValueType = HealthIndicesInfo.HEALTH_TYPE_BLOODSUGER;
            bloodSuger.mTypebloodSuger = GlobalSettings.getInstance().mBloodSugerType;


//				UploadHealthInfoRequest request = new UploadHealthInfoRequest(this);
//				request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;
//				request.reqOrgId = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityid;
//				request.reqOrgName = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityname;
//				request.reqType = HealthIndicesInfo.HEALTH_TYPE_BLOODSUGER;
//				request.reqHealthInfo = bloodSuger;
//				request.reqSession = "";
//
//				RequestManager.getInstance().sendRequest(request);
            Log.i(TAG, "sendRequest!");
            //}
        }

        return true;
    }

//	public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
//		// TODO Auto-generated method stub
//
//		Log.i(TAG, "sendRequest response!");
//		Log.e(TAG, "上传血糖数据成功！");
//
//		mMeasureStep = MEASURE_STEP_END;
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
//							&& (listener instanceof Sino_WL1ServiceListener) == true) {
//						Sino_WL1ServiceListener etcListener = (Sino_WL1ServiceListener) listener;
//						etcListener.onUpdateBloodSuger(Sino_WL1Service.this);
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
//		Log.e(TAG, "上传血数糖据失败！error"+ request.errorString);
//
//		mMeasureStep = MEASURE_STEP_END;
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
//						Sino_WL1ServiceListener etcListener = (Sino_WL1ServiceListener) listener;
//						etcListener.onUpdateError(Sino_WL1Service.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}


}

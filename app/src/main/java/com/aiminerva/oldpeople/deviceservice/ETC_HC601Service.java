package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.bean.BloodSugerInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC601B;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;

import java.util.List;

public class ETC_HC601Service extends BaseHealthService implements PrtEtc_HC601B.PrtEtc_HC601BListener {

    // --reponse data
    public PrtEtc_HC601B.PrtBloodSugerModel repBloodSugerModel;
    private String TAG = "ETC_HC601Service";

    public interface ETC_HC601ServiceListener extends BaseHealthServiceListener {
        void onUpdateBloodSuger(BloodSugerInfo bloodSuger);

        void onUpdateError(ETC_HC601Service sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtEtc_HC601B();
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

        PrtEtc_HC601B prtObject = (PrtEtc_HC601B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

//		PrtEtc_HC601B prtObject = (PrtEtc_HC601B) super.mPrtObject;
//		prtObject.masterSnd(send);
    }

    public boolean onMasterReceive(byte command, PrtEtc_HC601B.PrtBloodSugerModel result) {
        // TODO Auto-generated method stub
        final BloodSugerInfo bloodSuger = new BloodSugerInfo();
        bloodSuger.mBloodSuger = result.mBloodSuger;
        bloodSuger.mUDID = PrtEtc_HC601B.UDID;
        bloodSuger.mValueType = HealthIndicesInfo.HEALTH_TYPE_BLOODSUGER;
        bloodSuger.mTypebloodSuger = GlobalSettings.getInstance().mBloodSugerType;

        final List<BaseHealthServiceListener> listeners = mListeners;

        if (listeners.size() <= 0) {
            return false;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof ETC_HC601ServiceListener) == true) {
                        ETC_HC601ServiceListener etcListener = (ETC_HC601ServiceListener) listener;
                        etcListener.onUpdateBloodSuger(bloodSuger);
                    }
                }
            }

        };
        h.sendEmptyMessage(1);
        Log.i(TAG, "sendRequest!");


        return true;
    }

//	public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
//		// TODO Auto-generated method stub
//
//		Log.i(TAG, "sendRequest response!");
//		Log.e(TAG, "上传血糖数据成功！");
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
//							&& (listener instanceof ETC_HC601ServiceListener) == true) {
//						ETC_HC601ServiceListener etcListener = (ETC_HC601ServiceListener) listener;
//						etcListener.onUpdateBloodSuger(ETC_HC601Service.this);
//					}
//
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
//		Log.e(TAG, "上传血数糖据失败！error"+ request.errorString);
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
//							&& (listener instanceof ETC_HC601ServiceListener) == true) {
//						ETC_HC601ServiceListener etcListener = (ETC_HC601ServiceListener) listener;
//						etcListener.onUpdateError(ETC_HC601Service.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}
}

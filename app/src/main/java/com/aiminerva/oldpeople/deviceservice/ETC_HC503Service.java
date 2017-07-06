package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC503B;

import java.util.List;

public class ETC_HC503Service extends BaseHealthService implements PrtEtc_HC503B.PrtEtc_HC503BListener {

    // --reponse data
    public PrtEtc_HC503B.PrtBloodPressModel repBloodPressModel;
    private String TAG = "ETC_HC503Service";

    public interface ETC_HC503ServiceListener extends BaseHealthServiceListener {
        void onUpdateBloodPress(PrtEtc_HC503B.PrtBloodPressModel result);

        void onUpdateError(ETC_HC503Service sevice, String sErr);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtEtc_HC503B();
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

        PrtEtc_HC503B prtObject = (PrtEtc_HC503B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

//		PrtEtc_HC503B prtObject = (PrtEtc_HC503B) super.mPrtObject;
//		prtObject.masterSend(send);
    }

    public boolean onMasterReceive(byte command, PrtEtc_HC503B.PrtBloodPressModel sresult) {
        // TODO Auto-generated method stub
        final List<BaseHealthServiceListener> listeners = mListeners;
        final PrtEtc_HC503B.PrtBloodPressModel result = sresult;

        if (listeners.size() <= 0) {
            return false;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof ETC_HC503ServiceListener) == true) {
                        ETC_HC503ServiceListener etcListener = (ETC_HC503ServiceListener) listener;
                        etcListener.onUpdateBloodPress(result);
                    }
                }
            }

        };
        h.sendEmptyMessage(1);


        return true;
    }

	/*public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "sendRequest response!");
		Log.e(TAG, "上传血压数据成功！");
		
		final List<BaseHealthServiceListener> listeners = super.mListeners;
		
		if ( listeners.size() <=0 ){
			return;
		}
		
		Handler h = new Handler(Looper.getMainLooper()){

			public void handleMessage(Message arg0) {
				// TODO Auto-generated method stub
				
				Log.d(TAG, "listeners "+ listeners.size());
				for (BaseHealthServiceListener listener : listeners) {
					if (listener != null
							&& (listener instanceof ETC_HC503ServiceListener) == true) {
						ETC_HC503ServiceListener etcListener = (ETC_HC503ServiceListener) listener;
						etcListener.onUpdateBloodPress(ETC_HC503Service.this);
					}
				}
			}
			
		};
		h.sendEmptyMessage(1);

	}
	
	public void onRequestError(RequestBase request) {
		// TODO Auto-generated method stub
		Log.e(TAG, "上传血压数据失败！error"+ request.errorString);
		
		final List<BaseHealthServiceListener> listeners = super.mListeners;
		
		if ( listeners.size() <=0 ){
			return;
		}
		
		final String errorString = request.errorString;
		
		Handler h = new Handler(Looper.getMainLooper()){

			public void handleMessage(Message arg0) {
				// TODO Auto-generated method stub
				
				Log.d(TAG, "listeners "+ listeners.size());
				for (BaseHealthServiceListener listener : listeners) {
					if (listener != null
							&& (listener instanceof ETC_HC503ServiceListener) == true) {
						ETC_HC503ServiceListener etcListener = (ETC_HC503ServiceListener) listener;
						etcListener.onUpdateError(ETC_HC503Service.this, errorString);
					}
				}
			}
			
		};
		h.sendEmptyMessage(1);
	}*/

}

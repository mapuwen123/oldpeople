package com.aiminerva.oldpeople.deviceservice;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.bean.BloodPressInfo;
import com.aiminerva.oldpeople.bean.FinltopInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.deviceservice.prt.PrtFt_BP_88A;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;

import java.util.List;

public class FT_BP_88AService extends BaseHealthService implements PrtFt_BP_88A.PrtFt_BP_88AListener {

    // --reponse data
    private String TAG = "FT_BP_88AService";

    public interface FT_BP_88AServiceListener extends BaseHealthServiceListener {
        void onUpdateBloodPress(FT_BP_88AService sevice);

        void onUpdateError(FT_BP_88AService sevice, String sErr);

        void onMeasureStart(FT_BP_88AService sevice);

        void onMeasureEnd(FT_BP_88AService sevice);

        void onUpdateECG(FT_BP_88AService sevice);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtFt_BP_88A();
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
    public void onConnected(BluetoothChatService blueToothChat) {
        super.onConnected(blueToothChat);

        PrtFt_BP_88A prtObject = (PrtFt_BP_88A) super.mPrtObject;
        super.mPrtObject.init();
        prtObject.sendSsdp();
    }

    @Override
    public void onChatMsgRecv(BluetoothChatService blueToothChat, byte[] recv) {
        // TODO Auto-generated method stub
        super.onChatMsgRecv(blueToothChat, recv);

        PrtFt_BP_88A prtObject = (PrtFt_BP_88A) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);
    }

    @Override
    public boolean onMasterSend(byte[] buf) {
        mBluetoothChat.write(buf);
        return true;
    }

    @Override
    public boolean onMasterReceive(byte command, PrtFt_BP_88A.PrtFTBloodPressModel result) {
        // TODO Auto-generated method stub

        if (command == PrtBase.CMD_BLOODPRESSVALUE) {
//			GlobalSettings.getInstance().mFT_DeviceMap.put(3, this.getAddress());
            boolean r = false;
            FinltopInfo finltop = GlobalSettings.getInstance().getFinltopInfo();
            List<String> finltopBloodPress = finltop.getFinltop3();
            for (String mac : finltopBloodPress) {
                if (mac == this.getAddress()) {
                    r = true;
                    break;
                }
            }
            if (!r) {
                finltopBloodPress.add(this.getAddress());
                finltop.setFinltop3(finltopBloodPress);
            }
            GlobalSettings.getInstance().saveFinltopInfo(finltop);

            BloodPressInfo bloodPress = new BloodPressInfo();
            bloodPress.mDiastolicPressure = result.mDiastolicPressure;
            bloodPress.mSystolicPressure = result.mSystolicPressure;
            bloodPress.mPluse = result.mPluse;
            bloodPress.mValueType = HealthIndicesInfo.HEALTH_TYPE_BLOODPRESS;
            bloodPress.mUDID = PrtFt_BP_88A.UDID;

//			UploadHealthInfoRequest request = new UploadHealthInfoRequest(this);
//			request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;
//			request.reqOrgId = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityid;
//			request.reqOrgName = GlobalSettings.getInstance().getUserInfo().mCommunity.mCommunityname;
//			request.reqType = HealthIndicesInfo.HEALTH_TYPE_BLOODPRESS;
//			request.reqHealthInfo = bloodPress;
//			request.reqSession = "";
//
//			RequestManager.getInstance().sendRequest(request);
            Log.i(TAG, "sendRequest!");
        } else {
//			GlobalSettings.getInstance().mFT_DeviceMap.put(2, this.getAddress());
            boolean r = false;
            FinltopInfo finltop = GlobalSettings.getInstance().getFinltopInfo();
            List<String> finltopEgc = finltop.getFinltop2();
            for (String mac : finltopEgc) {
                if (mac == this.getAddress()) {
                    r = true;
                    break;
                }
            }
            if (!r) {
                finltopEgc.add(this.getAddress());
                finltop.setFinltop2(finltopEgc);
            }
            GlobalSettings.getInstance().saveFinltopInfo(finltop);//保存Finltop设备Mac列表

//			UploadECGFileRequest request = new UploadECGFileRequest(this);
//			request.reqUserId = GlobalSettings.getInstance().mCurrentRelationMember;
//			request.reqECGFile = result.mFilePath;
//			Log.e("ecg","uid = " + GlobalSettings.getInstance().mCurrentRelationMember);
//			Log.e("ecg","mFilePath = " + result.mFilePath);
//
//			RequestManager.getInstance().sendRequest(request);
        }

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
//							&& (listener instanceof FT_BP_88AServiceListener) == true) {
//						FT_BP_88AServiceListener ftListener = (FT_BP_88AServiceListener) listener;
//						ftListener.onUpdateBloodPress(FT_BP_88AService.this);
//					}
//
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}
//
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
//							&& (listener instanceof FT_BP_88AServiceListener) == true) {
//						FT_BP_88AServiceListener etcListener = (FT_BP_88AServiceListener) listener;
//						etcListener.onUpdateECG(FT_BP_88AService.this);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//
//	}
//	public void onRequestError(RequestBase request) {
//		// TODO Auto-generated method stub
//
//		if (request instanceof UploadECGFileRequest){
//			Log.e(TAG, "上传心电数据失败！error"+ request.errorString);
//		}else if (request instanceof UploadHealthInfoRequest){
//			Log.e(TAG, "上传血压数据失败！error"+ request.errorString);
//		}
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
//							&& (listener instanceof FT_BP_88AServiceListener) == true) {
//						FT_BP_88AServiceListener etcListener = (FT_BP_88AServiceListener) listener;
//						etcListener.onUpdateError(FT_BP_88AService.this, errorString);
//					}
//				}
//			}
//
//		};
//		h.sendEmptyMessage(1);
//	}

    @Override
    public boolean onMasterStart() {
        // TODO Auto-generated method stub
        final List<BaseHealthServiceListener> listeners = super.mListeners;

        if (listeners.size() <= 0) {
            return false;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof FT_BP_88AServiceListener) == true) {
                        FT_BP_88AServiceListener ftListener = (FT_BP_88AServiceListener) listener;
                        ftListener.onMeasureStart(FT_BP_88AService.this);
                    }

                }
            }

        };
        h.sendEmptyMessage(1);

        return true;
    }

    @Override
    public boolean onMasterEnd() {
        // TODO Auto-generated method stub
        final List<BaseHealthServiceListener> listeners = super.mListeners;

        if (listeners.size() <= 0) {
            return false;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof FT_BP_88AServiceListener) == true) {
                        FT_BP_88AServiceListener ftListener = (FT_BP_88AServiceListener) listener;
                        ftListener.onMeasureEnd(FT_BP_88AService.this);
                    }

                }
            }

        };
        h.sendEmptyMessage(1);
        return true;
    }

}

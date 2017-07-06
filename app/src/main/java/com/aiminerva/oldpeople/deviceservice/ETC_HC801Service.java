package com.aiminerva.oldpeople.deviceservice;

//import javax.security.auth.callback.Callback;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aiminerva.oldpeople.bean.BloodOxgenInfo;
import com.aiminerva.oldpeople.bean.HealthIndicesInfo;
import com.aiminerva.oldpeople.database.helper.ETC_HC801DeviceHelper;
import com.aiminerva.oldpeople.deviceservice.prt.PrtEtc_HC801B;
import com.aiminerva.oldpeople.globalsettings.GlobalSettings;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ETC_HC801Service extends BaseHealthService implements
        PrtEtc_HC801B.PrtEtc_HC801BListener {

    // --reponse data
    private String TAG = "ETC_HC801Service";

    public PrtEtc_HC801B.PrtOxgenModel repOxgenModel;

    private List<BloodOxgenInfo> mOxgenModelSource;
    private List<BloodOxgenInfo> mAverageSource;
    private final int AVERAGE_LENGTH = 2;
    public static boolean isTested = false;

    public interface ETC_HC801ServiceListener extends BaseHealthServiceListener {
        void onUpdateOxgen(long mOxgenValue, long mPlusState);

        void onUpdateOxgenFailed(ETC_HC801Service sevice, String errMessage);
    }

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        if (super.init() == false) {
            return false;
        }
        super.mPrtObject = new PrtEtc_HC801B();
        super.mPrtObject.setListener(this);

        super.DEVICE_NAME = "血氧仪";
        mOxgenModelSource = new ArrayList<BloodOxgenInfo>();
        mAverageSource = new ArrayList<BloodOxgenInfo>();

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

        PrtEtc_HC801B prtObject = (PrtEtc_HC801B) super.mPrtObject;
        prtObject.masterRec(recv);
    }

    @Override
    public void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send) {
        // TODO Auto-generated method stub
        super.onChatMsgSent(blueToothChat, send);

//		PrtEtc_HC801B prtObject = (PrtEtc_HC801B) super.mPrtObject;
//		prtObject.masterSnd(send);
    }

    @Override
    public void onConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onConnected(blueToothChat);
        super.mPrtObject.init();
        mAverageSource.clear();
        isTested = false;
    }

    @Override
    public void onConnectFailed(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onConnectFailed(blueToothChat);
        super.mPrtObject.uinit();
        mAverageSource.clear();
    }

    @Override
    public void onDisConnected(BluetoothChatService blueToothChat) {
        // TODO Auto-generated method stub
        super.onDisConnected(blueToothChat);
        super.mPrtObject.uinit();

        // send HealthInfo
        // final BloodOxgenInfo averageValue = this.averageOxgenValue(
        // mAverageSource );
        //
        // mAverageSource.clear();
        // if ( averageValue == null ){
        // Log.i(TAG, "averageValue is NULL!");
        // return ;
        // }
        // ETC_HC801DeviceHelper.getInstance().storeDeviceData(
        // averageValue.mUserId , averageValue.mOxgenValue,
        // averageValue.mPlusState );

        //this.sendUploadHealthsRequest();
        ETC_HC801DeviceHelper.getInstance().removeDeviceAllData();
        // this.sendUploadHealthsRequest();
    }

    public boolean onMasterReceive(byte command, final PrtEtc_HC801B.PrtOxgenModel result) {
        // TODO Auto-generated method stub

        BloodOxgenInfo health = new BloodOxgenInfo();
        health.mDate = System.currentTimeMillis();
        health.mUDID = PrtEtc_HC801B.UDID;
        health.mUserId = GlobalSettings.getInstance().mCurrentRelationMember;
        health.mOxgenValue = result.oxgen;
        health.mPlusState = result.pulseRate;
        health.mValueType = HealthIndicesInfo.HEALTH_TYPE_OXGEN;

        Log.e(TAG, "receive oxgenModel:" + health.mOxgenValue
                + " plusStateModel:" + health.mPlusState);

        mOxgenModelSource.add(health);
        if (mOxgenModelSource.size() <= AVERAGE_LENGTH) {
            return false;
        }
        Log.e(TAG, "OxgenModelSource data count = " + mOxgenModelSource.size());

        final BloodOxgenInfo averageValue = this
                .averageOxgenValue(mOxgenModelSource);

        mOxgenModelSource.clear();
        if (averageValue == null) {
            Log.e(TAG, "averageValue is null ");
            return false;
        }

        // Log.e(TAG, "OxgenModelSource oxgen value " + averageValue.mOxValue +
        // "oxgen refValue = " + averageValue.mOOxValue + "plus value" +
        // averageValue.mPlusBState + "plus refvalue" +
        // averageValue.mPlusBState);
        mAverageSource.add(averageValue);
        if (mAverageSource != null && mAverageSource.size() > 0) {
            Log.e(TAG, "即将上传");
            sendUploadHealthsRequest();
        }



		/*if (mAverageSource.size() == 5) {

			// send HealthInfo
			final BloodOxgenInfo averageValueInfo = this
					.averageOxgenValue(mAverageSource);

			mAverageSource.clear();
			if (averageValueInfo == null) {
				Log.i(TAG, "averageValue is NULL!");
				mAverageSource.clear();
				return true;
			}
			ETC_HC801DeviceHelper.getInstance().storeDeviceData(
					averageValueInfo.mUserId, averageValueInfo.mOxgenValue,
					averageValueInfo.mPlusState);

		}*/
        return true;
    }

    private void sendUploadHealthsRequest() {
        if (isTested) return;

        if (mAverageSource.size() == 0) {
            Logger.e("没有数据");
            return;
        }


        final List<BaseHealthServiceListener> listeners = mListeners;

        if (listeners.size() <= 0) {
            return;
        }

        Handler h = new Handler(Looper.getMainLooper()) {

            public void handleMessage(Message arg0) {
                // TODO Auto-generated method stub

                Log.d(TAG, "listeners " + listeners.size());
                for (BaseHealthServiceListener listener : listeners) {
                    if (listener != null
                            && (listener instanceof ETC_HC801ServiceListener) == true) {
                        Log.d("OnUploadHealthInfo", "upload");

                        ETC_HC801ServiceListener etcListener = (ETC_HC801ServiceListener) listener;
                        etcListener.onUpdateOxgen(mAverageSource.get(0).mOxgenValue, mAverageSource.get(0).mPlusState);
                        isTested=true;//无论上传成功与否 都会禁止重新上传
                    }
                }
            }

        };
        h.sendEmptyMessage(1);


    }


//    public void onRequestError(RequestBase request) {
//        // TODO Auto-generated method stub
//        Log.e(TAG, "上传血氧数据失败！");
//
//        final List<BaseHealthServiceListener> listeners = super.mListeners;
//
//        if (listeners.size() <= 0) {
//            return;
//        }
//
//        Handler h = new Handler(Looper.getMainLooper()) {
//
//            public void handleMessage(Message arg0) {
//                // TODO Auto-generated method stub
//
//                Log.d(TAG, "listeners " + listeners.size());
//                for (BaseHealthServiceListener listener : listeners) {
//                    if (listener != null
//                            && (listener instanceof ETC_HC801ServiceListener) == true) {
//                        ETC_HC801ServiceListener etcListener = (ETC_HC801ServiceListener) listener;
//                        etcListener.onUpdateOxgenFailed(ETC_HC801Service.this,
//                                "上传血氧数据失败！");
//                    }
//                }
//            }
//
//        };
//        h.sendEmptyMessage(1);
//
//    }
//
//    public void OnUploadHealthInfo(UploadHealthInfoRequest request) {
//        // TODO Auto-generated method stub
//        // ETC_HC801DeviceHelper.getInstance().removeDeviceData(request.reqHealthInfo);
//
//        Log.i(TAG, "sendRequest response!");
//        Log.e(TAG, "上传血氧数据成功！");
//
//        final List<BaseHealthServiceListener> listeners = super.mListeners;
//
//        if (listeners.size() <= 0) {
//            return;
//        }
//
//        Handler h = new Handler(Looper.getMainLooper()) {
//
//            public void handleMessage(Message arg0) {
//                // TODO Auto-generated method stub
//
//                Log.d(TAG, "listeners " + listeners.size());
//                for (BaseHealthServiceListener listener : listeners) {
//                    if (listener != null
//                            && (listener instanceof ETC_HC801ServiceListener) == true) {
//                        Log.d("OnUploadHealthInfo", "upload");
//
//                        ETC_HC801ServiceListener etcListener = (ETC_HC801ServiceListener) listener;
//                        etcListener.onUpdateOxgen(ETC_HC801Service.this);
//                    }
//                }
//            }
//
//        };
//        h.sendEmptyMessage(1);
//
//    }

    private BloodOxgenInfo averageOxgenValue(List<BloodOxgenInfo> bloodOxgens) {
        if (bloodOxgens.size() <= AVERAGE_LENGTH) {
            return null;
        }
        int averageOxgenValue = 0;
        int averagePlusState = 0;
        for (BloodOxgenInfo info : bloodOxgens) {
            averageOxgenValue += info.mOxgenValue;
            averagePlusState += info.mPlusState;
        }
        averageOxgenValue = averageOxgenValue / bloodOxgens.size();
        averagePlusState = averagePlusState / bloodOxgens.size();

        int i = 0;
        List<BloodOxgenInfo> newInfos = new ArrayList<BloodOxgenInfo>();

        for (BloodOxgenInfo info : bloodOxgens) {
            // if ( Math.abs(info.mOxgenValue - averageOxgenValue) > 20 ||
            // Math.abs(info.mPlusState - averagePlusState) > 50 ){
            // continue;
            // }
            newInfos.add(info);
        }

        if (newInfos.size() < 1) {
            return null;
        }

        int oxgenValue = 0;
        int plusStateValue = 0;
        for (BloodOxgenInfo validInfo : newInfos) {
            oxgenValue += validInfo.mOxgenValue;
            plusStateValue += validInfo.mPlusState;
        }
        oxgenValue = oxgenValue / newInfos.size();
        plusStateValue = plusStateValue / newInfos.size();

        BloodOxgenInfo averInfo = new BloodOxgenInfo();
        BloodOxgenInfo lastInfo = newInfos.get(newInfos.size() - 1);
        averInfo.mDate = lastInfo.mDate;
        averInfo.mHasSent = lastInfo.mHasSent;
        averInfo.mMsgId = lastInfo.mMsgId;
        averInfo.mOxgenValue = oxgenValue;
        averInfo.mPlusState = plusStateValue;
        averInfo.mUDID = lastInfo.mUDID;
        averInfo.mUserId = lastInfo.mUserId;
        averInfo.mValueType = lastInfo.mValueType;
        return averInfo;
    }
}

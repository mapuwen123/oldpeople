package com.aiminerva.oldpeople.ui.heat;

import com.aiminerva.oldpeople.ui.login.bean.UserBean;
import com.raiing.data.RealTemperature;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2017/6/27.
 */

public class HeatModel {
    private Realm realm;

    public HeatModel() {
        realm = Realm.getDefaultInstance();
    }

//    public void saveHeatToRealm(List<RealTemperature> datas) {
//        realm.executeTransactionAsync(realm1 -> {
//            RealmResults<RealTemperature> heats = realm1.where(RealTemperature.class).findAll();
//            if (heats.size() != 0) {
//                heats.deleteAllFromRealm();
//            }
//            if (datas.size() != 0) {
//                for (int i = 0; i < datas.size(); i++) {
//                    RealTemperature heat = realm1.createObject(RealTemperature.class);
//                    heat.setTime(datas.get(i).getTime());
//                    heat.setTempeature(datas.get(i).getTempeature());
//                }
//            }
//        });
//    }
//
//    public List<RealTemperature> getHeatFromRealm() {
//        RealmQuery<RealTemperature> query = realm.where(RealTemperature.class);
//        RealmResults<RealTemperature> heats = query.findAll();
//        return heats;
//    }

    public void closeRealm() {
        realm.close();
    }
}

package com.aiminerva.oldpeople.bean;

import java.io.Serializable;

public class BloodOxgenInfo extends HealthIndicesInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5675594845650510555L;
    public long mOxgenValue;// 实际血氧值
    public long mPlusState;// 实际脉搏值

    public int level;



}

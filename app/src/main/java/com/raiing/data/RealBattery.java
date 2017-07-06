package com.raiing.data;

/**
 * 实时的电池电量
 *
 * @author jun.wang@raiing.com.
 * @since 2015/4/29
 */
public class RealBattery {
    private int battery = -1;

    public RealBattery(int battery) {
        this.battery = battery;
    }

    public int getBattery() {
        return battery;
    }

}

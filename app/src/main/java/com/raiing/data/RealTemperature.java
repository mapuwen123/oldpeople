package com.raiing.data;

/**
 * 实时温度
 *
 * @author jun.wang@raiing.com.
 * @since 2015/4/28
 */
public class RealTemperature {
    private long time;
    private int tempeature;

    public RealTemperature() {

    }

    public RealTemperature(long time, int tempeature) {
        this.time = time;
        this.tempeature = tempeature;
    }

    public int getTempeature() {
        return tempeature;
    }

    public void setTempeature(int tempeature) {
        this.tempeature = tempeature;
    }

    public long getTime() {

        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

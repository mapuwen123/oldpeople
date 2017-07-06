package com.aiminerva.oldpeople.bluetooth4.creative.libdemo;

import android.os.Handler;

/**
 * Created by Aaron on 2017/2/16.
 */

public class BaseThread  extends Thread
{
    protected Handler mHandler;
    protected boolean stop = false;
    protected boolean pause = false;

    public void Stop() {
        this.stop = true;
    }

    public void Pause() {
        this.pause = true;
    }

    public synchronized void Continue()
    {
        this.pause = false;
        notify();
    }

    public void setHandler(Handler _handler) {
        this.mHandler = _handler;
    }
}
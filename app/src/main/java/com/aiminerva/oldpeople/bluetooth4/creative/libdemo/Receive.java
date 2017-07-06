package com.aiminerva.oldpeople.bluetooth4.creative.libdemo;

import com.creative.base.Ireader;

import java.util.Vector;

/**
 * Created by Aaron on 2017/2/16.
 */

public class Receive
{
    private Ireader is;
    private RecvThread recvThread;
    public static Vector<Byte> originalData = new Vector();
    private IECGCallBack callBack;

    public Receive(Ireader is, IECGCallBack callBack)
    {
        this.is = is;
        this.callBack = callBack;
    }

    public void Start()
    {
        if (this.recvThread != null) {
            this.recvThread.Stop();
            this.recvThread = null;
        }
        this.recvThread = new RecvThread();
        this.recvThread.setName("receive thread");
        this.recvThread.start();
    }

    public void Stop()
    {
        if (this.recvThread != null) {
            this.recvThread.Stop();
            this.recvThread = null;
        }
    }

    public void Pause()
    {
        if (this.recvThread != null)
            this.recvThread.Pause();
    }

    public void Continue()
    {
        cleanData();
        if (this.recvThread != null)
            this.recvThread.Continue();
    }

    public void cleanData()
    {
        if (originalData != null)
            originalData.clear();
    }

    protected class RecvThread extends Thread
    {
        private boolean stop = false;

        private boolean pause = false;

        private byte[] buffer = new byte[''];

        protected RecvThread() {
        }
        public void run() { super.run();
            try {
                synchronized (this) {
                    while (!this.stop) {
                        if (this.pause) {
                            wait();
                        }

                        int len = Receive.this.is.read(this.buffer);

                        for (int i = 0; i < len; i++) {
                            Receive.originalData.add(Byte.valueOf(this.buffer[i]));
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Receive.this.callBack.OnConnectLose();
                e.printStackTrace();
            }
        }

        public void Stop()
        {
            this.stop = true;
        }

        public boolean isStop() {
            return this.stop;
        }

        public void Pause() {
            this.pause = true;
        }

        public synchronized void Continue() {
            this.pause = false;
            notify();
        }
    }
}
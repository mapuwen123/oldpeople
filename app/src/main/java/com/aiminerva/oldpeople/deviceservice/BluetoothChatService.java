/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aiminerva.oldpeople.deviceservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
//import com.android.BluetoothChat.prt.PrtSino;
//import com.android.BluetoothChat.prt.PrtSino.PrtData;
//import com.android.BluetoothChat.prt.PrtSino.PrtSinoListener;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {
    // Debuggings
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    //    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Integer mState;
    private BluetoothChatServiceListener listener = null;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private Boolean mIsReConnect = true;
    private Boolean mCancel;
    private Handler mHTimer;

    // Constants that indicate the current connection state
    public class EnumBlutToothState {
        public static final int STATE_NONE = 0;       // we're doing nothing
        public static final int STATE_LISTEN = 1;     // now listening for incoming connections
        public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
        public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    }

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = EnumBlutToothState.STATE_NONE;
        mHTimer = new Handler();
        mSocket = null;
        mCancel = false;
//        mHandler = handler;
    }

    public BluetoothChatService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = EnumBlutToothState.STATE_NONE;
        mHTimer = new Handler();
        mSocket = null;
        mCancel = false;
    }

    public BluetoothChatService(Boolean autoconnet) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = EnumBlutToothState.STATE_NONE;
        mHTimer = new Handler();
        mSocket = null;
        mIsReConnect = autoconnet;
        mCancel = false;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();

    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(EnumBlutToothState.STATE_LISTEN);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        mDevice = device;
        // Cancel any thread attempting to make a connection
        if (mState == EnumBlutToothState.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

//        mPrtSino.setListener(this);
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, this);
        mConnectThread.start();
        setState(EnumBlutToothState.STATE_CONNECTING);

        if (getListener() != null) {
            getListener().onConnecting(this);
        }
        mCancel = false;
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, this);
        mConnectedThread.start();

        setState(EnumBlutToothState.STATE_CONNECTED);

        if (getListener() != null) {
            getListener().onConnected(this);
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        mCancel = true;
        mIsReConnect = false;

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(EnumBlutToothState.STATE_NONE);

        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (getListener() != null) {
            getListener().onDisConnected(this);
        }
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != EnumBlutToothState.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(EnumBlutToothState.STATE_LISTEN);
        Log.i(TAG, "Unable to connect device!");
//        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        if (getListener() != null) {
            getListener().onConnectFailed(this);
        }
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(EnumBlutToothState.STATE_LISTEN);

        if (getListener() != null) {
            getListener().onDisConnected(this);
        }
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != EnumBlutToothState.STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case EnumBlutToothState.STATE_LISTEN:
                            case EnumBlutToothState.STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case EnumBlutToothState.STATE_NONE:
                            case EnumBlutToothState.STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private BluetoothChatService mmService;

        public ConnectThread(BluetoothDevice device, BluetoothChatService service) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mmService = service;

            //tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            //Method m = null;
//			try {
//				//m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
//			} catch (NoSuchMethodException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

            try {
                //tmp = (BluetoothSocket) m.invoke(device, 1);
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mmSocket = tmp;
            mSocket = mmSocket;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");

            //Looper.prepare();
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            synchronized (mCancel) {
                if (mCancel == true) {
                    return;
                }
            }

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                if (mmSocket != null)
                    mmSocket.connect();

//                if (mmService.listener != null ){
//                	mmService.listener.onConnected(mmService);
//                }

            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                    //mmSocket = null;
//                    mPrtSino.uninit();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
//                BluetoothChatService.this.start();

                BluetoothChatService.this.setState(EnumBlutToothState.STATE_CONNECTING);

                //mmSocket = null;
                mHTimer.postDelayed(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        synchronized (mCancel) {
                            if (mCancel == true) {
                                return;
                            }
                        }

                        try {
                            BluetoothChatService.this.connect(BluetoothChatService.this.mDevice);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                            return;
                        }
                    }
                }, 1000);

                //BluetoothChatService.this.connect( BluetoothChatService.this.mDevice);
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);

            //Looper.loop();
        }

        public void cancel() {
            try {
                mCancel = true;
                mmSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothChatService mmService;

        public ConnectedThread(BluetoothSocket socket, BluetoothChatService service) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmService = service;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (Exception e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            //Looper.prepare();
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                synchronized (mCancel) {
                    if (mCancel == true) {

                        try {
                            mmInStream.close();
                            mmOutStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    byte[] sBuffer = new byte[bytes];
                    for (int i = 0; i < bytes; i++) {
                        sBuffer[i] = buffer[i];
                    }

                    if (mmService.listener != null) {
                        mmService.listener.onChatMsgRecv(mmService, sBuffer);
                    }

                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
            Log.e(TAG, "connectedThread is canceling!");

            //reconnect 1s 
            try {
                mmSocket.close();
                //mmSocket = null;
            } catch (IOException e) {
                // TODO: handle exception
                Log.e(TAG, "closesocket exception :" + e.getMessage());
            }

            if (mmService.mIsReConnect) {
                Log.e(TAG, "reconnecting !");

                mHTimer.postDelayed(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        BluetoothChatService.this.connect(BluetoothChatService.this.mDevice);
                    }
                }, 300);

            }

            //Looper.loop();
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                if (mmService.listener != null) {
                    mmService.listener.onChatMsgSent(mmService, buffer);
                }

                // Share the sent message back to the UI Activity
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    synchronized public BluetoothChatServiceListener getListener() {
        return listener;
    }

    synchronized public void setListener(BluetoothChatServiceListener listener) {
        this.listener = listener;
    }
}

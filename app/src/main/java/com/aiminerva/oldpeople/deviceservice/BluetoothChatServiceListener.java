package com.aiminerva.oldpeople.deviceservice;

/**
 * BluetoothChatServiceListener
 *
 * @author cgq 12/31
 */
public interface BluetoothChatServiceListener {
    /**
     * onConnnecting listener
     *
     * @param blueToothChat
     */
    void onConnecting(BluetoothChatService blueToothChat);

    /**
     * onConnected listener
     *
     * @param blueToothChat
     */
    void onConnected(BluetoothChatService blueToothChat);

    /**
     * onDisConnected listener
     *
     * @param blueToothChat
     */
    void onDisConnected(BluetoothChatService blueToothChat);

    /**
     * onListening lisener ,use as bluetooth service ,
     *
     * @param blueToothChat
     */
    void onListening(BluetoothChatService blueToothChat);

    /**
     * onConnectFailed listener
     *
     * @param blueToothChat
     */
    void onConnectFailed(BluetoothChatService blueToothChat);

    /**
     * onChatMsgRecv listener
     *
     * @param blueToothChat
     * @param recv
     */
    void onChatMsgRecv(BluetoothChatService blueToothChat, byte[] recv);

    /**
     * onChatMsgSent listener
     *
     * @param blueToothChat
     * @param send
     */
    void onChatMsgSent(BluetoothChatService blueToothChat, byte[] send);

}

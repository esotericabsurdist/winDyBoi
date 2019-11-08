package com.spaceshipfreehold.windyboi;

public interface IWinchConnectionListener {
    void onConnected();
    void onDisconnected();
    void onSocketConnectionFailed();
    void onNoBluetoothAdapterFound();
    void onBluetoothNotEnabled();
    void onWinchNotFound();
}

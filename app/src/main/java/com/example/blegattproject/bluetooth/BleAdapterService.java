package com.example.blegattproject.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor; import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Binder;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Message;

public class BleAdapterService extends Service{

    private BluetoothAdapter bluetooth_adapter;
    private BluetoothGatt bluetooth_gatt;
    private BluetoothManager bluetooth_manager;
    private Handler activity_handler = null;
    private BluetoothDevice device;
    private BluetoothGattDescriptor descriptor;
    private boolean connected = false;
    public boolean alarm_playing = false;
    private final IBinder binder = new LocalBinder();
    public boolean isConnected(){
        return connected;
    }
    @Override
    public void OnCreate(){
        if(bluetooth_manager == null) {
            bluetooth_manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetooth_manager == null)
                return;
        }
        bluetooth_adapter = bluetooth_manager.getAdapter();
        if(bluetooth_adapter == null)
            return;
    }

    public class LocalBinder extends Binder {
        public BleAdapterService getService() {
            return BleAdapterService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    // messages sent back to activity
    public static final int GATT_CONNECTED = 1;
    public static final int GATT_DISCONNECT = 2;
    public static final int GATT_SERVICES_DISCOVERED = 3;
    public static final int GATT_CHARACTERISTIC_READ = 4;
    public static final int GATT_CHARACTERISTIC_WRITTEN = 5;
    public static final int GATT_REMOTE_RSSI = 6;
    public static final int MESSAGE = 7;
    public static final int NOTIFICATION_OR_INDICATION_RECEIVED = 8;
    // message parms
    public static final String PARCEL_DESCRIPTOR_UUID = "DESCRIPTOR_UUID";
    public static final String PARCEL_CHARACTERISTIC_UUID = "CHARACTERISTIC_UUID"; public static final String PARCEL_SERVICE_UUID = "SERVICE_UUID";
    public static final String PARCEL_VALUE = "VALUE";
    public static final String PARCEL_RSSI = "RSSI";
    public static final String PARCEL_TEXT = "TEXT";

    public void setActivityHandler(Handler handler) {
        activity_handler = handler;
    }

    private void sendConsoleMessage(String text) {
        Message msg = Message.obtain(activity_handler, MESSAGE);
        Bundle data = new Bundle();
        data.putString(PARCEL_TEXT, text);
        msg.setData(data);
        msg.sendToTarget();
    }


}

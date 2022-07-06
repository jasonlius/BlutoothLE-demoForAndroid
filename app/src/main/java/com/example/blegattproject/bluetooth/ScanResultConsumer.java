package com.example.blegattproject.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface ScanResultConsumer {
    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi);
    public void scanningStarted();
    public void scanningStopped();
}

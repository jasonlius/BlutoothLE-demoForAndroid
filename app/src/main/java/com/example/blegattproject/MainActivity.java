package com.example.blegattproject;

import androidx.appcompat.app.AppCompatActivity;
import com.example.blegattproject.bluetooth.ScanResultConsumer;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ScanResultConsumer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void candidateDevice(BluetoothDevice device, byte[] scan_record, int rssi) {
    }

    @Override
    public void scanningStarted() {
    }

    @Override
    public void scanningStopped() {
    }
}
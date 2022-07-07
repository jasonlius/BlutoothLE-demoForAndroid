package com.example.blegattproject.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.blegattproject.Constants;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private BluetoothLeScanner scanner = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private Handler handler= new Handler();
    private ScanResultConsumer scanResultConsumer;
    private Context context;
    private boolean scanning = false;
    private String deviceNameStart = "";

    public Scanner(Context context) {
        this.context = context;
        //首先获取蓝牙服务。检查系统蓝牙开启状态
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d(Constants.TAG, "蓝牙设备尚未开启");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.FEATURE_BLUETOOTH) {
//                Toast.makeText(context.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
//                return;
//            }
            context.startActivity(enableBtIntent);
        }
        Log.d(Constants.TAG, "蓝牙已开启");
    }

    public void startScanning(final ScanResultConsumer scanResultConsumer, long stopAfterMs) {
        if (scanning) {
            Log.d(Constants.TAG, "已经启动扫描");
            return;
        }
        if (scanner == null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            Log.d(Constants.TAG, "创建bluetoothleScanner object.");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            if (scanning) {
                Log.d(Constants.TAG, "Stopping scanning"); scanner.stopScan(scan_callback);
                setScanning(false);
            }} }, stopAfterMs);
        this.scanResultConsumer = scanResultConsumer;
        Log.d(Constants.TAG,"扫描中");
        List<ScanFilter> filters;
        filters = new ArrayList<ScanFilter>();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        setScanning(true);
        scanner.startScan(filters, settings, scan_callback);
    }


    private ScanCallback scan_callback = new ScanCallback() {
        public void onScanResult(int callbackType, final ScanResult result) {
            if (!scanning) {
                return;
            }
            scanResultConsumer.candidateDevice(result.getDevice(), result.getScanRecord().getBytes(), result.getRssi());
        } };

    public  void  stopScanning(){
        setScanning(false);
        Log.d(Constants.TAG, "停止扫描中");
        scanner.stopScan(scan_callback);
    }
    //停止扫描和开始扫描的功能
    void setScanning(boolean scanning) {
        this.scanning = scanning;
        if (!scanning) {
            scanResultConsumer.scanningStopped();
        } else {
            scanResultConsumer.scanningStarted();
        }
    }

    public boolean isScanning(){
        return scanning;
    }
}
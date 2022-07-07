package com.example.blegattproject.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.blegattproject.R;
import com.example.blegattproject.Constants;
import com.example.blegattproject.ui.MainActivity;
import com.example.blegattproject.bluetooth.Scanner;
import com.example.blegattproject.bluetooth.*;
import com.example.blegattproject.bluetooth.ScanResultConsumer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ScanResultConsumer {

    private boolean ble_scanning = false;
    private Handler handler = new Handler();
    private ListAdapter ble_device_list_adapter;
    private Scanner ble_scanner;
    private static final long SCAN_TIMEOUT = 5000;
    private static final int REQUEST_LOCATION = 0;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION}; private boolean permissions_granted=false;
    private int device_count=0;
    private Toast toast;
    static class ViewHolder {
        public TextView text;
        public TextView bdaddr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonText();
        ble_device_list_adapter = new ListAdapter();
        ListView listView = (ListView) this.findViewById(R.id.deviceList);
        listView.setAdapter(ble_device_list_adapter);
        ble_scanner = new Scanner(this.getApplicationContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (ble_scanning) {
                    ble_scanner.stopScanning();
                }

                BluetoothDevice device = ble_device_list_adapter.getDevice(position);
                if (toast != null) {
                    toast.cancel();
                }
                Intent intent = new Intent(MainActivity.this, PeripheralControlActivity.class);
                intent.putExtra(PeripheralControlActivity.EXTRA_NAME, device.getName());
                intent.putExtra(PeripheralControlActivity.EXTRA_ID, device.getAddress());
                startActivity(intent);

            }
        });
    }

    private void setButtonText() {
        String text="";
        text = Constants.FIND;
        final String button_text = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) MainActivity.this.findViewById(R.id.scanButton)).setText(button_text);
            }
        });
    }

    private void setScanState(boolean value) {
        ble_scanning = value;
        Log.d(Constants.TAG,"Setting scan state to "+value);
        ((Button) this.findViewById(R.id.scanButton)).setText(value ? Constants.STOP_SCANNING : Constants.FIND);
    }

    private class ListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> ble_devices;

        public ListAdapter() {
            super();
            ble_devices = new ArrayList<BluetoothDevice>();
        }

        public void addDevice(BluetoothDevice device) {
            if (!ble_devices.contains(device)) {
                ble_devices.add(device);
            }
        }

        public boolean contains(BluetoothDevice device) {
            return ble_devices.contains(device);
        }

        public BluetoothDevice getDevice(int position) {
            return ble_devices.get(position);
        }

        public void clear() {
            ble_devices.clear();
        }

        @Override
        public int getCount() {
            return ble_devices.size();
        }

        @Override
        public Object getItem(int i) {
            return ble_devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = MainActivity.this.getLayoutInflater().inflate(R.layout.list_row, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) view.findViewById(R.id.textView);
                viewHolder.bdaddr = (TextView) view.findViewById(R.id.bdaddr);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = ble_devices.get(i);
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.text.setText(deviceName);
            } else {
                viewHolder.text.setText("unknown device");
            }
            viewHolder.bdaddr.setText(device.getAddress());
            return view;
        }
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


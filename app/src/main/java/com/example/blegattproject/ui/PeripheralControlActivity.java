package com.example.blegattproject.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.blegattproject.bluetooth.BleAdapterService;
import com.example.blegattproject.R;

import java.util.Timer;

public class PeripheralControlActivity extends Activity {
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";
    private BleAdapterService bluetooth_le_adapter;
    private String device_name;
    private String device_address;
    private Timer mTimer;
    private boolean sound_alarm_on_disconnect = false;
    private int alert_level;
    private boolean back_requested = false;
    private boolean share_with_server = false;
    private Switch share_switch;

    private final ServiceConnection service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(message_handler); }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler message_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            byte[] b = null;

            switch (msg.what) {
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                    break;
            }
        }
    };

    public void onLow(View view) {
    }
    public void onMid(View view) {
    }
    public void onHigh(View view) {
    }
    public void onNoise(View view) {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_control);

        // read intent data
        final Intent intent = getIntent();
        device_name = intent.getStringExtra(EXTRA_NAME);
        device_address = intent.getStringExtra(EXTRA_ID);

        // show the device name
        ((TextView) this.findViewById(R.id.nameTextView))
                .setText("Device : " + device_name + " [" + device_address + "]");
        // hide the coloured rectangle used to show green/amber/red rssi distance
        ((LinearLayout) this.findViewById(R.id.rectangle)).setVisibility(View.INVISIBLE);

        // hide the coloured rectangle used to show green/amber/red rssi
        // distance
        ((LinearLayout) this.findViewById(R.id.rectangle)).setVisibility(View.INVISIBLE);

        // disable the noise button
        ((Button) PeripheralControlActivity.this.findViewById(R.id.noiseButton)).setEnabled(false);

        // disable the LOW/MID/HIGH alert level selection buttons
        ((Button) this.findViewById(R.id.lowButton)).setEnabled(false);
        ((Button) this.findViewById(R.id.midButton)).setEnabled(false);
        ((Button) this.findViewById(R.id.highButton)).setEnabled(false);

        share_switch = (Switch) this.findViewById(R.id.switch1);
        share_switch.setEnabled(false);
        share_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bluetooth_le_adapter != null) {
                    share_with_server = isChecked;
                    if (!isChecked && bluetooth_le_adapter.isConnected()) {
                        showMsg("Switched off sharing proximity data");
                        // write 0,0 to cause peripheral device to switch off all LEDs
                        if (bluetooth_le_adapter.writeCharacteristic(
                                BleAdapterService.PROXIMITY_MONITORING_SERVICE_UUID,
                                BleAdapterService.CLIENT_PROXIMITY_CHARACTERISTIC, new byte[] { 0, 0 })) {
                        } else {
                            showMsg("Failed to inform peripheral sharing has been disabled");
                        }
                    }
                }
            }
        });

        temperature_switch = (Switch) this.findViewById(R.id.switch2);
        temperature_switch.setEnabled(false);
        temperature_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bluetooth_le_adapter != null && bluetooth_le_adapter.isConnected()) {
                    if (!isChecked) {
                        showMsg("Switching off temperature monitoring");
                        if (bluetooth_le_adapter.setIndicationsState(BleAdapterService.HEALTH_THERMOMETER_SERVICE_UUID,
                                BleAdapterService.TEMPERATURE_MEASUREMENT_CHARACTERISTIC, false)) {
                            clearTemperature();
                        } else {
                            showMsg("Failed to inform temperature monitoring has been disabled");
                        }
                    } else {
                        showMsg("Switching on temperature monitoring");
                        if (bluetooth_le_adapter.setIndicationsState(BleAdapterService.HEALTH_THERMOMETER_SERVICE_UUID,
                                BleAdapterService.TEMPERATURE_MEASUREMENT_CHARACTERISTIC, true)) {
                        } else {
                            showMsg("Failed to inform temperature monitoring has been enabled");
                        }
                    }
                }
            }
        });

        // connect to the Bluetooth adapter service
        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, service_connection, BIND_AUTO_CREATE);

        showMsg("READY");
    }


}

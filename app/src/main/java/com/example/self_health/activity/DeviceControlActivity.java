package com.example.self_health.activity;

import android.os.Bundle;


import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.self_health.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.result.DataTypeResult;

import static java.security.AccessController.getContext;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceType;
    private Calendar mCalendar;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private boolean recordingFlag = false;

    private GoogleApiClient mGoogleApiClient;
    DataSource bpmSource;
    DataSet    bpmDataset;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String str_dat = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(str_dat);
                Log.d("Received Data:", str_dat);

            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mCalendar = Calendar.getInstance();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceType = intent.getStringExtra("measurement_type");

        mGoogleApiClient = MainActivity.mClient;

        if(mDeviceType.equals(getString(R.string.BODY_TEMP_type))) {
            getActionBar().setIcon(R.drawable.temperature);
        }
        else {
            getActionBar().setIcon(R.mipmap.ic_heart);

            // if BPM, create dataset to pu to google FIT
            bpmSource = new DataSource.Builder()
                    .setAppPackageName(this)
                    .setDataType(DataType.TYPE_HEART_RATE_BPM)
                    .setStreamName(TAG + "bpm")
                    .setType(DataSource.TYPE_RAW)
                    .build();

            bpmDataset = DataSet.create(bpmSource);
        }

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                try {
                    mBluetoothLeService.connect(mDeviceAddress);
        }
                catch (Exception e){
                    Log.e(TAG, "ERROR connecting to the device");
                }
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
            if(recordingFlag){
                //long meas_time = mCalendar.getTimeInMillis();
                Toast.makeText(mBluetoothLeService, "Rec: " + data + " : " , Toast.LENGTH_SHORT).show();

                if(mDeviceType.equals(getString(R.string.HR_type))){
                    // saving to the google fit database
                    Date now = new Date();
                    mCalendar.setTime(now);
                    long timemilis = mCalendar.getTimeInMillis();

                    DataPoint bpm_now = DataPoint.create(bpmSource);
                    bpm_now.setTimestamp(timemilis, TimeUnit.MILLISECONDS);
                    bpm_now.getValue(Field.FIELD_BPM).setFloat(70);

                    bpmDataset.add(bpm_now);

                    // @todo save HR to the json local database
                }
                else{
                    // @todo save BODY_TEMP to the json local database
                }

            }
        }
    }

    // Iterates through the supported GATT Services/Characteristics.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            // Find Heart Rate service (0x180D)
            if (SampleGattAttributes.lookup(uuid, "unknown").equals("Heart Rate Service")) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                // Loops through available Characteristics
                for (BluetoothGattCharacteristic gattCharacteristic :
                        gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    // Find Heart rate measurement (0x2A37)
                    if (SampleGattAttributes.lookup(uuid, "unknown").equals("Heart Rate Measurement")) {
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    }

                }
            } else if (SampleGattAttributes.lookup(uuid, "unknown").equals("Body Temp Service")) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                // Loops through available Characteristics
                for (BluetoothGattCharacteristic gattCharacteristic :
                        gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    // Find Heart rate measurement (0x2A37)
                    if (SampleGattAttributes.lookup(uuid, "unknown").equals("Body Temp Measurement")) {
                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    }
                }
            }
        }
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void toggleRecording( View view){
        Log.d(TAG, "Recording turned " + (recordingFlag ? "OFF":"ON") );
        recordingFlag ^= true;

        // if it was turned off
        if(!recordingFlag){
            if(mDeviceType.equals(getString(R.string.BODY_TEMP_type))) {
                // @todo save to the json local database
            }
            else { // if BPM
                //Save in google fit
                try {
                    // @todo save to the json local database

                    Fitness.HistoryApi.insertData(mGoogleApiClient, bpmDataset).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            // Before querying the data, check to see if the insertion succeeded.

                            if (!status.isSuccess()) {
                                String sta = status.getStatusMessage();
                                Log.d(TAG, "FAILED SAVING TO GOOGLE FIT");

                            }
                            else {
                                // At this point, the data has been inserted and can be read.
                                Log.d(TAG, "SUCESS SAVING TO GOOGLE FIT");
                            }
                        }

                    });

                    Log.d(TAG, "Saved" + bpmDataset.toString() );
                    Toast.makeText(this, "Saved" + Integer.toString(bpmDataset.getDataPoints().size()) + " elements to the database.", Toast.LENGTH_SHORT).show();
                    // clear the dataset
                    bpmDataset = DataSet.create(bpmSource);
                }
                catch(Exception e)
                {
                    Log.d(TAG, "Error saving" + bpmDataset.toString() );
                    Toast.makeText(this, "Error saving " + Integer.toString(bpmDataset.getDataPoints().size()) + " elements to the database.", Toast.LENGTH_SHORT).show();
                    // clear the dataset
                    bpmDataset = DataSet.create(bpmSource);
                }


            }
        }


    }
}

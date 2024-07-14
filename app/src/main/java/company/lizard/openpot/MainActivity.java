package company.lizard.openpot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    private String btDeviceAddress;
    BLEService bleService;
    private BluetoothLeScanner bluetoothLeScanner;
    final String TAG = MainActivity.class.getSimpleName();;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final long SCAN_PERIOD = 10000;
    private boolean scanning = false;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }
        AtomicBoolean haveLocationPerms = new AtomicBoolean(false);
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                haveLocationPerms.set(true);
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                            }
                        }
                );
        if(!haveLocationPerms.get()){
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            });
            builder.show();
        }
        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
                }
            });
            builder.show();
        }
        //bleService.initialize();
        if (btManager == null) {
            btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (btManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
            }
        }

        btAdapter = btManager.getAdapter();
        if (btAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }
    }
    public void connectToDevice(View view){
        bluetoothLeScanner = btAdapter.getBluetoothLeScanner();
        scanLeDevice();
        if(mConnectionState == STATE_DISCONNECTED){

        }
        else{
            disconnect();
        }
    }
    public void command(View view){
        Button btn = (Button)view;
        String cmdText = btn.getText().toString();
        Intent intent = new Intent(getApplicationContext(), CommandSettings.class);
        intent.putExtra("command", cmdText);
        startActivity(intent);
        //bleService.manual(30, Pressure.HIGH, Timer.NONE,0);
    }
    public void cancel(View view){ bleService.cancel(); }
    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            Log.i(TAG, "Started Scan");
            scanning = true;
            ScanSettings.Builder settings = new ScanSettings.Builder();
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }
    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if( result.getDevice().getName() != null && result.getDevice().getName().equals("Instant Pot Smart")){
                        btDeviceAddress = result.getDevice().getAddress();
                        BluetoothDevice device = btAdapter.getRemoteDevice(btDeviceAddress);
                        if (device == null) {
                            Log.w(TAG, "Device not found.  Unable to connect.");
                        }
                        if(bleService == null){
                            bleService = BLEService.getInstance(getApplicationContext());
                        }
                        device.createBond();
                        bleService.connect(device).enqueue();
                        Log.i(TAG, "Connected Instant Pot bluetooth");
                        //bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }
            };
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (btAdapter == null || bleService == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bleService.disconnect();
    }
}
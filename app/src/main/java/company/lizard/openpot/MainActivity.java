package company.lizard.openpot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    BLEService bleService;
    private BluetoothLeScanner bluetoothLeScanner;
    final String TAG = MainActivity.class.getSimpleName();
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final long SCAN_PERIOD = 10000;
    private boolean scanning = false;
    private Handler handler = new Handler();
    //private DataReceiver receiver = new DataReceiver(); // Create the receiver
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // requests for bluetooth to be turned on
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
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
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1));
            builder.show();
        }
        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
        connectDevice();
    }
    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intFilter = new IntentFilter("company.lizard.openpot.TELEMETRY_NOTIFY");
        intFilter.addAction("company.lizard.openpot.CONNECTED");
        intFilter.addAction("company.lizard.openpot.DISCONNECTED");
        intFilter.addAction("company.lizard.openpot.TWENTY_FOUR");
        Intent test = ContextCompat.registerReceiver(this, dataReceiver, intFilter, ContextCompat.RECEIVER_EXPORTED); // Register receiver
        //Log.i(TAG, test.toString());
    }
    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(dataReceiver);
    }
    public void connectToDevice(View view){
        connectDevice();
    }
    public void connectDevice(){
        bluetoothLeScanner = btAdapter.getBluetoothLeScanner();
        scanLeDevice();
    }
    public void command(View view){
        Button btn = (Button)view;
        String cmdText = btn.getText().toString();
        Intent intent = new Intent(getApplicationContext(), CommandSettings.class);
        intent.putExtra("command", cmdText);
        startActivity(intent);
        //bleService.manual(30, Pressure.HIGH, Timer.NONE,0);
    }
    public void settings(View view){
        Intent intent = new Intent(getApplicationContext(), AppSettings.class);
        startActivity(intent);
        //bleService.is24Hr();
    }
    public void cancel(View view){
        if(bleService.isReady() && bleService.isConnected()){
            bleService.cancel();
        }

    }
    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
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
                        String btDeviceAddress = result.getDevice().getAddress();
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
    BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent){
            if (intent.getAction().equals("company.lizard.openpot.CONNECTED")){
                ImageView connectionIndic = findViewById(R.id.icnIndicator);
                connectionIndic.setImageResource(R.drawable.openpot_active);
            }
            else if(intent.getAction().equals("company.lizard.openpot.DISCONNECTED")){
                ImageView connectionIndic = findViewById(R.id.icnIndicator);
                connectionIndic.setImageResource(R.drawable.openpot);
            }
            else if(intent.getAction().equals("company.lizard.openpot.TWENTY_FOUR")){
                byte[] data = intent.getByteArrayExtra("VALUE");
                if(data[0] == 0){
                    Log.d(TAG, "24 hour");
                }
                else{
                    Log.d(TAG, "12 hour");
                }

            }
            else{
                byte[] data = intent.getByteArrayExtra("TELEMETRY");
                if(data[2] == (byte)0x40){
                    TextView timeRemaining = findViewById(R.id.txtRemainingTime);
                    TextView workMode = findViewById(R.id.txtWorkMode);
                    TextView temperature = findViewById(R.id.txtTemperature);
                    TextView pressureLevel = findViewById(R.id.txtPressureLevel);
                    TextView heatingLevel = findViewById(R.id.txtHeatingLevel);
                    switch((int)data[4]){
                        case 0xE:
                            workMode.setText(R.string.warm);
                            break;
                        case 0xD:
                            workMode.setText(R.string.warm);
                            break;
                        case 0xC:
                            workMode.setText(R.string.on);
                            break;
                        case 0xB:
                            workMode.setText(R.string.timer);
                            break;
                        default:
                            workMode.setText(R.string.off);
                            break;
                    }
                    int pressureLvl = (int)data[11] >> 4 & 15;
                    switch(pressureLvl){
                        case 5:
                            pressureLevel.setText(R.string.lid_open);
                            break;
                        case 9:
                            pressureLevel.setText(R.string.none);
                            break;
                        case 10: // 0xAx
                            pressureLevel.setText(R.string.low);
                            break;
                        case 11: // 0xBx
                            pressureLevel.setText(R.string.high);
                            break;
                        default:
                            pressureLevel.setText(R.string.none);
                            break;
                    }
                    double heatingLvl = (int)data[13] / 16.0 * 100.0;
                    String heatingTxt = Math.round(heatingLvl) + "%";
                    byte[] adData = new byte[4];
                    adData[0] = data[12];
                    int adVal = bytesToInt(adData);
                    int temp = Integer.parseInt(TemperatureHelper.fromADToC(String.valueOf(adVal)));
                    SharedPreferences sharedPreferences = getSharedPreferences("OPENPOT_PREFS",MODE_PRIVATE);
                    boolean isCelsius = sharedPreferences.getBoolean("IS_CELSIUS", true);
                    if(!isCelsius){
                        temp = (int)Math.round(temp * 1.8 + 32);
                    }
                    String unit = isCelsius ? "C": "F";
                    temperature.setText(String.format(Locale.US,"%dº" + unit,temp));
                    heatingLevel.setText(heatingTxt);
                    int mins = data[10];
                    int hrs = data[9];
                    String timeRemain;
                    if(hrs == 0){
                        timeRemain = String.valueOf(mins) + 'm';
                    }
                    else{
                        timeRemain = hrs  + ":" + mins;
                    }

                    timeRemaining.setText(timeRemain);
                }
                Log.i("BR", toHex(ByteBuffer.wrap(data)));
            }
        }
    };
    public static String toHex(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            sb.append(String.format("%02X", bb.get()));
        }
        return sb.toString();
    }
    public static int bytesToInt(byte[] bytes) {
        if (bytes == null) {
            return 0;
        }
        int ret = 0;
        int[] ints = new int[4];
        int i = 0;
        while (i < bytes.length && i < ints.length) {
            ints[i] = bytes[i] & 255;
            i++;
        }
        for (int i2 = 0; i2 < ints.length; i2++) {
            ret |= ints[i2] << (i2 * 8);
        }
        return ret;
    }
}
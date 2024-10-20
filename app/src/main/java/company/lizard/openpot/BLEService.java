package company.lizard.openpot;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.observer.ConnectionObserver;
enum Pressure{
    LOW,
    HIGH
}
enum Mode{
    LESS,
    NORMAL,
    MORE
}
enum Yogurt{
    PASTEURIZE,
    YOGURT,
    FERMENT
}
enum Timer{
    TIMER1,
    TIMER2,
    NONE
}
enum CommandType{
    SOUP,
    STEW,
    CHILI,
    POULTRY,
    STEAM,
    PORRIDGE,
    MULTIGRAIN
}
public class BLEService extends BleManager implements ConnectionObserver {
    final String TAG = BLEService.class.getSimpleName();
    final private UUID OPENPOT_SERVICE_UUID = UUID.fromString("0000dab0-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_CHAR_UUID = UUID.fromString("0000dab1-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_NOTIFY_UUID = UUID.fromString("0000dab2-0000-1000-8000-00805F9B34FB");

    final private UUID OPENPOT_TIME_SERVICE_UUID = UUID.fromString("0000daa0-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_CLOCK_UUID = UUID.fromString("0000daa1-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_TIMER1_UUID = UUID.fromString("0000daa2-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_TIMER2_UUID = UUID.fromString("0000daa3-0000-1000-8000-00805F9B34FB");
    private final UUID OPENPOT_24HR_UUID = UUID.fromString("0000daa4-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic openPotControlPoint;
    private BluetoothGattCharacteristic openPot24hrBit;
    private BluetoothGattCharacteristic openPotClock;
    private BluetoothGattCharacteristic openPotTelemetry;
    private BluetoothGattCharacteristic openPotTimer1;
    private BluetoothGattCharacteristic openPotTimer2;
    private static BLEService instance;
    public BLEService(@NonNull final Context context){
        super(context);
        setConnectionObserver(this);
    }

    public static synchronized BLEService getInstance(Context context){
        if(instance == null){
            instance = new BLEService(context);
        }
        return instance;
    }
    // Duration 0 - 120m
    // Pressure L/H
    // Delay
    public void durationPressureMode(int duration, Pressure pressure, Timer timer, int delay, Mode mode, CommandType cmd){
        byte[] cmdBase = hexStringToByteArray("aa555a010020a00000001e0000000000000000");
        switch(cmd){
            case SOUP:
                cmdBase[4] = 0x0A;
                break;
            case STEW:
                cmdBase[4] = 0x09;
                break;
            case CHILI:
                cmdBase[4] = 0x08;
                break;
            case POULTRY:
                cmdBase[4] = 0x07;
                break;
            case STEAM:
                cmdBase[4] = 0x04;
                break;
            case PORRIDGE:
                cmdBase[4] = 0x03;
                break;
            case MULTIGRAIN:
                cmdBase[4] = 0x02;
        }
        // seems like mode and pressure are interlinked. Base mode at low pressure is a, less is e and more is 6, and then high pressure is one added to each.
        if(mode == Mode.LESS){
            cmdBase[6] = (byte) 0xe0;
        }
        else if(mode == Mode.NORMAL){
            cmdBase[6] = (byte)0x60;
        }
        else if(mode == Mode.MORE){
            cmdBase[6] = (byte)0xa0;
        }
        if(pressure == Pressure.HIGH){
            cmdBase[6] += (byte)0x10;
        }
        cmdBase[9] = (byte)(duration / 60);
        cmdBase[10] = (byte)(duration % 60);
        if(timer == Timer.TIMER1){
            cmdBase[5] = 0x11;
            cmdBase[6] = (byte) (delay / 60);
            cmdBase[7] = (byte) (delay % 60);
        }
        else if(timer == Timer.TIMER2){
            cmdBase[5] = 0x12;
            cmdBase[6] = (byte) (delay / 60);
            cmdBase[7] = (byte) (delay % 60);
        }
        calCheckCode(cmdBase);
        writeCharacteristic(openPotControlPoint, cmdBase, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
        Log.i(TAG, "Duration Pressure Mode");
    }
    public void manual(int duration, Pressure pressure, Timer timer, int delay){
        byte[] manual = hexStringToByteArray("aa555a010c20300000001e0000000000000000");
        if(pressure == Pressure.LOW) {
            manual[6] = 0x20;
        }
        manual[9] = (byte)(duration / 60);
        manual[10] = (byte)(duration % 60);
        if(timer == Timer.TIMER1){
            manual[5] = 0x11;
            manual[7] = (byte) (delay / 60);
            manual[8] = (byte) (delay % 60);
        }
        else if(timer == Timer.TIMER2){
            manual[5] = 0x12;
            manual[7] = (byte) (delay / 60);
            manual[8] = (byte) (delay % 60);
        }
        calCheckCode(manual);
        writeCharacteristic(openPotControlPoint, manual, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
    }
    // Duration 0 - 99h59m
    // Mode L/N/M
    // Delay
    public void keepWarm(int duration, Mode mode, Timer timer, int delay){
        byte[] manual = hexStringToByteArray("aa555a010d204000000a000000000000000000");
        manual[9] = (byte)(duration / 60);
        manual[10] = (byte)(duration % 60);
        if(Mode.LESS == mode){
            manual[6] = (byte)0xc0;
        }
        else if(Mode.MORE == mode){
            manual[6] = (byte)0x80;
        }
        if(timer == Timer.TIMER1){
            manual[5] = 0x11;
            manual[7] = (byte) (delay / 60);
            manual[8] = (byte) (delay % 60);
        }
        else if(timer == Timer.TIMER2){
            manual[5] = 0x12;
            manual[7] = (byte) (delay / 60);
            manual[8] = (byte) (delay % 60);
        }
        calCheckCode(manual);
        writeCharacteristic(openPotControlPoint, manual, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
    }
    public void yogurt(Yogurt yogurt, int duration){
        byte[] yog = hexStringToByteArray("aa555a01052040000008000000000000000000");
        if(yogurt == Yogurt.PASTEURIZE){
            yog[6] = (byte)0xc0;
        }
        else if(yogurt == Yogurt.YOGURT){
            yog[6] = (byte)0x40;
        }
        else if(yogurt == Yogurt.FERMENT){
            yog[6] = (byte)0x80;
        }
        yog[9] = (byte)(duration / 60);
        yog[10] = (byte)(duration % 60);
        calCheckCode(yog);
        writeCharacteristic(openPotControlPoint, yog, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
        Log.i(TAG, toHex(ByteBuffer.wrap(yog)));
    }
    public void rice(Pressure pressure, Timer timer, int delay){
        byte[] rice = hexStringToByteArray("aa555a01012030000000000000000000000000");
        if(pressure == Pressure.LOW) {
            rice[6] = 0x20;
        }
        if(timer == Timer.TIMER1){
            rice[5] = 0x11;
            rice[7] = (byte) (delay / 60);
            rice[8] = (byte) (delay % 60);
        }
        else if(timer == Timer.TIMER2){
            rice[5] = 0x12;
            rice[7] = (byte) (delay / 60);
            rice[8] = (byte) (delay % 60);
        }
        calCheckCode(rice);
        writeCharacteristic(openPotControlPoint, rice, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE).enqueue();
    }
    public void setTime(){
        // Define the custom epoch start date and time
        LocalDateTime customEpoch = LocalDateTime.of(2001, 1, 1, 0, 0, 0);

        // Get the current local date and time
        LocalDateTime now = LocalDateTime.now();

        // Convert both times to seconds since epoch
        long customEpochSeconds = customEpoch.toEpochSecond(ZoneOffset.UTC);
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);

        // Calculate the difference in seconds
        int secondsSinceCustomEpoch = (int)(nowSeconds - customEpochSeconds);

        // Convert the difference to a byte array in little-endian order
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(secondsSinceCustomEpoch);
        writeCharacteristic(openPotClock, buffer.array(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE).fail((@NonNull()BluetoothDevice device, int status) ->{
            Log.d(TAG, "Sync time failed " + status);
        }).enqueue();

    }
    public void is24Hr(){
        readCharacteristic(openPot24hrBit).with((device, data) -> {
            Intent intent = new Intent("company.lizard.openpot.TWENTY_FOUR");
            intent.putExtra("VALUE", data.getValue());
            OPApplication.getContext().sendBroadcast(intent);
            Log.d(TAG, "Read 24 hr");
        }).fail((@NonNull() BluetoothDevice device, int status) -> {
            Log.d(TAG, "24 hr read failed " + status);
        }).enqueue();
    }
    public void set24Hr(boolean is24Hr){
        byte[] milTime = new byte[1];
        milTime[0] = (byte)(is24Hr ? 0 : 1);
        writeCharacteristic(openPot24hrBit, milTime, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE).fail((@NonNull()BluetoothDevice device, int status) ->{
            Log.d(TAG, "24 hr write failed " + status);
        }).enqueue();
    }
    public void getTimer1(){
        readCharacteristic(openPotTimer1).with((device, data) -> {
            Intent intent = new Intent("company.lizard.openpot.TIMER1");
            intent.putExtra("VALUE", data.getValue());
            OPApplication.getContext().sendBroadcast(intent);
        }).fail((@NonNull() BluetoothDevice device, int status) -> {
            Log.d(TAG, "Timer 1 read failed " + status);
        }).enqueue();
    }
    public void getTimer2(){
        readCharacteristic(openPotTimer2).with((device, data) -> {
            Intent intent = new Intent("company.lizard.openpot.TIMER2");
            intent.putExtra("VALUE", data.getValue());
            OPApplication.getContext().sendBroadcast(intent);
        }).fail((@NonNull() BluetoothDevice device, int status) -> {
            Log.d(TAG, "Timer 2 read failed " + status);
        }).enqueue();
    }
    public void setTimer1(int timer){
        byte[] timerBytes = new byte[2];
        timerBytes[0] = (byte)(timer / 60);
        timerBytes[1] = (byte)(timer % 60);
        writeCharacteristic(openPotTimer1, timerBytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE).enqueue();
    }
    public void setTimer2(int timer){
        byte[] timerBytes = new byte[2];
        timerBytes[0] = (byte)(timer / 60);
        timerBytes[1] = (byte)(timer % 60);
        writeCharacteristic(openPotTimer2, timerBytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE).enqueue();
    }

    public void getTime(){
        readCharacteristic(openPotClock).with((device, data) -> {
            Intent intent = new Intent("company.lizard.openpot.CLOCK");
            intent.putExtra("VALUE", data.getValue());
            OPApplication.getContext().sendBroadcast(intent);
        }).enqueue();
    }


    public void calCheckCode(byte[] bytes) {
        byte checksum = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            checksum = (byte) (bytes[i] + checksum);
        }
        bytes[19] = (byte) ((checksum ^ 255) + 1);
    }
    public void cancel(){
        byte[] cancel = hexStringToByteArray("aa555a010e0000000000000000000000000000");
        calCheckCode(cancel);
        writeCharacteristic(openPotControlPoint, cancel, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2 + 1]; // need to leave room for check char
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String toHex(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            sb.append(String.format("%02X", bb.get()));
        }
        return sb.toString();
    }


    @Override
    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt){
        BluetoothGattService openPotService = gatt.getService(OPENPOT_SERVICE_UUID);
        BluetoothGattService openPotTimeService = gatt.getService(OPENPOT_TIME_SERVICE_UUID);
        if(openPotService != null && openPotTimeService != null){
            openPotControlPoint = openPotService.getCharacteristic(OPENPOT_CHAR_UUID);
            openPotTelemetry = openPotService.getCharacteristic(OPENPOT_NOTIFY_UUID);
            openPot24hrBit = openPotTimeService.getCharacteristic(OPENPOT_24HR_UUID);
            openPotClock = openPotTimeService.getCharacteristic(OPENPOT_CLOCK_UUID);
            openPotTimer1 = openPotTimeService.getCharacteristic(OPENPOT_TIMER1_UUID);
            openPotTimer2 = openPotTimeService.getCharacteristic(OPENPOT_TIMER2_UUID);
            return true;
        }
        return false;
    }
    public void onDeviceConnecting(@NonNull final BluetoothDevice device){
        Log.i(TAG, "Device Connecting");
    }
    public void onDeviceDisconnecting(@NonNull final BluetoothDevice device){
        Log.i(TAG, "Device Disconnecting");
    }
    void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data){
        Intent intent = new Intent("company.lizard.openpot.TELEMETRY_NOTIFY");
        intent.putExtra("TELEMETRY", data.getValue());
        OPApplication.getContext().sendBroadcast(intent);
    }
    @SuppressLint("MissingPermission")
    public void onDeviceConnected(@NonNull final BluetoothDevice device){
        //broadcastUpdate(CONNECTED, device.getName());
        Intent intent = new Intent("company.lizard.openpot.CONNECTED");
        OPApplication.getContext().sendBroadcast(intent);
    }
    public void onDeviceFailedToConnect(@NonNull final BluetoothDevice device, int status){
        Log.i(TAG, "Device failed to connect");
    }
    public void onDeviceReady(@NonNull final BluetoothDevice device){
        setNotificationCallback(openPotTelemetry).with(this::onDataReceived);
        enableNotifications(openPotTelemetry).enqueue();
        Log.i(TAG, "Device Ready");
    }
    @SuppressLint("MissingPermission")
    public void onDeviceDisconnected(@NonNull final BluetoothDevice device, int status){
        //broadcastUpdate(DISCONNECTED, device.getName());
        Intent intent = new Intent("company.lizard.openpot.DISCONNECTED");
        OPApplication.getContext().sendBroadcast(intent);
    }
}

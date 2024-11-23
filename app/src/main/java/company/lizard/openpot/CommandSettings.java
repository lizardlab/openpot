package company.lizard.openpot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CommandSettings extends AppCompatActivity {
    private String cmd;
    final String TAG = MainActivity.class.getSimpleName();
    BLEService bleService;
    private Yogurt yogurt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmd_settings);
        bleService = BLEService.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cmd = extras.getString("command");
            TextView cmdLine = findViewById(R.id.lblCommand);
            cmdLine.setText(cmd);
            Log.d(TAG,cmd);
            TextView mode = findViewById(R.id.lblMode);
            EditText durationAmt = findViewById(R.id.txtDurationTime);
            RadioGroup modeBlk = findViewById(R.id.modeBlock);
            TextView pressure = findViewById(R.id.lblPressureLevel);
            RadioGroup pressureBlk = findViewById(R.id.pressureBlock);
            TextView duration = findViewById(R.id.lblDuration);
            LinearLayout durationBlk = findViewById(R.id.durationBlock);
            TextView yogurt = findViewById(R.id.lblYogurt);
            RadioGroup yogurtBlk = findViewById(R.id.yogurtBlock);
            if(cmd.equalsIgnoreCase("manual")){
                durationAmt.setText(R.string.thirty);
                durationBlk.setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
                pressureBlk.setVisibility(View.VISIBLE);
            }
            else if(cmd.equalsIgnoreCase("keep warm")){
                durationAmt.setText(R.string.ten);
                durationBlk.setVisibility(View.VISIBLE);

            }
            else if(cmd.equalsIgnoreCase("rice")){
                pressureBlk.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
            }
            else if(cmd.equalsIgnoreCase("yogurt")){
                Button delayBtn = findViewById(R.id.btnDelayStart);
                delayBtn.setVisibility(View.GONE);
                yogurtBlk.setVisibility(View.VISIBLE);
                yogurt.setVisibility(View.VISIBLE);
            }
            else{
                durationAmt.setText(R.string.thirty);
                durationBlk.setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
                pressureBlk.setVisibility(View.VISIBLE);
                modeBlk.setVisibility(View.VISIBLE);
                mode.setVisibility(View.VISIBLE);
            }
        }
        SharedPreferences sharedPref = getSharedPreferences("OPENPOT_PREFS", MODE_PRIVATE);
        findViewById(R.id.btnDelayStart).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            int timer1Val = sharedPref.getInt("TIMER1", 17);
            LocalTime timer1 = LocalTime.of(timer1Val / 60, timer1Val % 60);
            int timer2Val = sharedPref.getInt("TIMER2", 90);
            LocalTime timer2 = LocalTime.of(timer2Val / 60, timer2Val % 60);
            boolean is24hr = sharedPref.getBoolean("24", false);
            DateTimeFormatter format;
            if(is24hr){
                format = DateTimeFormatter.ofPattern("H:m");
            }
            else{
                format = DateTimeFormatter.ofPattern("h:m a");
            }
            popup.getMenu().add(0, R.id.timer1, 0, getText(R.string.timer1) + " (" + timer1.format(format) + ")");
            popup.getMenu().add(0, R.id.timer1, 1, getText(R.string.timer2) + " (" + timer2.format(format) + ")");
            //popup.getMenuInflater().inflate(R.menu.timer_menu, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.timer1) {
                    start(Timer.TIMER1);
                    return true;
                } else if (itemId == R.id.timer2) {
                    start(Timer.TIMER2);
                    return true;
                }
                return false;
            });
        });
    }

    public void startBtn(View view){
        start(Timer.NONE);
    }
    public void start(Timer timer){
        RadioGroup pressureBlk = findViewById(R.id.pressureBlock);
        EditText durationAmt = findViewById(R.id.txtDurationTime);
        int durationInt = Integer.parseInt(durationAmt.getText().toString());
        RadioButton selectedPressure = findViewById(pressureBlk.getCheckedRadioButtonId());
        RadioGroup modeBlk = findViewById(R.id.modeBlock);
        RadioButton selectedMode = findViewById(modeBlk.getCheckedRadioButtonId());
        Pressure pressure;
        Mode mode;
        if(selectedPressure.getText().equals("Low")){
            pressure = Pressure.LOW;
        }
        else{
            pressure = Pressure.HIGH;
        }
        if(selectedMode.getText().equals("Less")){
            mode = Mode.LESS;
        }
        else if(selectedMode.getText().equals("More")){
            mode = Mode.MORE;
        }
        else{
            mode = Mode.NORMAL;
        }
        HashMap<String, CommandType> cmdSet = new HashMap<>();
        cmdSet.put("soup", CommandType.SOUP);
        cmdSet.put("meat/stew", CommandType.STEW);
        cmdSet.put("bean/chili", CommandType.CHILI);
        cmdSet.put("poultry", CommandType.POULTRY);
        cmdSet.put("porridge", CommandType.PORRIDGE);
        cmdSet.put("steam", CommandType.STEAM);
        cmdSet.put("multigrain", CommandType.MULTIGRAIN);
        int delay = 0;
        SharedPreferences sharedPref = getSharedPreferences("OPENPOT_PREFS",MODE_PRIVATE);
        if(timer == Timer.TIMER1){
            delay = sharedPref.getInt("TIMER1", 17); // this was what I remembered the default was?
        }
        else if(timer == Timer.TIMER2){
            delay = sharedPref.getInt("TIMER2", 90);
        }
        if(cmd.equalsIgnoreCase("manual")){
            bleService.manual(durationInt, pressure, timer, delay);
        }
        else if(cmd.equalsIgnoreCase("rice")){
            bleService.rice(pressure, timer, delay);
        }
        else if(cmd.equalsIgnoreCase("keep warm")){
            bleService.keepWarm(durationInt, mode, timer, delay);
        }
        else if(cmd.equalsIgnoreCase("yogurt")){
            RadioGroup yogurtBlk = findViewById(R.id.yogurtBlock);
            RadioButton selectedYogurt = findViewById(yogurtBlk.getCheckedRadioButtonId());
            Yogurt yogurt;
            if(selectedYogurt.getText().equals("Pasteurize")){
                yogurt = Yogurt.PASTEURIZE;
            }
            else if(selectedYogurt.getText().equals("Ferment")){
                yogurt = Yogurt.FERMENT;
            }
            else {
                yogurt = Yogurt.YOGURT;
            }
            bleService.yogurt(yogurt, durationInt);
        }
        else if(cmd.equalsIgnoreCase("sauté")){
            bleService.saute(mode, timer, delay);
        }
        else{
            bleService.durationPressureMode(durationInt, pressure, timer, delay, mode, cmdSet.get(cmd.toLowerCase(Locale.ENGLISH)));
        }
    }

}
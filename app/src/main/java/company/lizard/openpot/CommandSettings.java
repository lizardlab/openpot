package company.lizard.openpot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Locale;

public class CommandSettings extends AppCompatActivity {
    private String cmd;
    final String TAG = MainActivity.class.getSimpleName();
    BLEService bleService;
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
            if(cmd.equalsIgnoreCase("manual")){
                durationAmt.setText("30");
                durationBlk.setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
                pressureBlk.setVisibility(View.VISIBLE);
            }
            else if(cmd.equalsIgnoreCase("keep warm")){
                durationAmt.setText("10");
                durationBlk.setVisibility(View.VISIBLE);

            }
            else if(cmd.equalsIgnoreCase("rice")){
                pressureBlk.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
            }
            else if(cmd.equalsIgnoreCase("yogurt")){
                Button delayBtn = findViewById(R.id.btnDelayStart);
                delayBtn.setVisibility(View.GONE);
            }
            else{
                durationAmt.setText("30");
                durationBlk.setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
                pressure.setVisibility(View.VISIBLE);
                pressureBlk.setVisibility(View.VISIBLE);
                modeBlk.setVisibility(View.VISIBLE);
                mode.setVisibility(View.VISIBLE);
            }
        }
    }
    public void start(View view){
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
        if(cmd.equalsIgnoreCase("manual")){
            bleService.manual(durationInt, pressure, Timer.NONE, 0);
        }
        else if(cmd.equalsIgnoreCase("rice")){
            bleService.rice(pressure, Timer.NONE, 0);
        }
        else if(cmd.equalsIgnoreCase("keep warm")){
            bleService.keepWarm(durationInt, mode, Timer.NONE, 0);
        }
        else{
            bleService.durationPressureMode(durationInt, pressure, Timer.NONE, 0, mode, cmdSet.get(cmd.toLowerCase(Locale.ENGLISH)));
        }

    }
}
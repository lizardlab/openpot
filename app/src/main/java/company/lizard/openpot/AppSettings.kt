package company.lizard.openpot

import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import java.util.Locale


class AppSettings : AppCompatActivity() {

    private lateinit var bleService: BLEService
    //val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref =  getSharedPreferences("OPENPOT_PREFS",Context.MODE_PRIVATE)
        val isCelsius = sharedPref.getBoolean("IS_CELSIUS", true)
        super.onCreate(savedInstanceState)
        bleService = BLEService.getInstance(applicationContext)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pot_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(isCelsius){
            val txtTempUnit = findViewById<TextView>(R.id.txtTempUnit)
            txtTempUnit.text = "ºC"
        }
        val twentyFourToggle = findViewById<SwitchCompat>(R.id.btnMilTime)
        twentyFourToggle.setOnCheckedChangeListener{ _, isChecked -> bleService.set24Hr(isChecked)}
        val txtTimer1 = findViewById<EditText>(R.id.txtTimer1)
        txtTimer1.doAfterTextChanged { text ->
            if(text!!.split(":").size == 2 && text!!.split(":")[0].isNotEmpty() && text!!.split(":")[1].length > 1){
                val hr = text!!.split(":")[0].toInt()
                val min = text!!.split(":")[1].toInt()
                val mins = hr * 60 + min
                bleService.setTimer1(mins)
            }
        }
        val txtTimer2 = findViewById<EditText>(R.id.txtTimer2)
        txtTimer2.doAfterTextChanged { text ->
            if(text!!.split(":").size == 2 && text!!.split(":")[0].isNotEmpty() && text!!.split(":")[1].length > 1) {
                val hr = text!!.split(":")[0].toInt()
                val min = text!!.split(":")[1].toInt()
                val mins = hr * 60 + min
                bleService.setTimer2(mins)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intFilter = IntentFilter("company.lizard.openpot.TWENTY_FOUR")
        intFilter.addAction("company.lizard.openpot.TIMER1")
        intFilter.addAction("company.lizard.openpot.TIMER2")
        registerReceiver(dataReceiver, intFilter, RECEIVER_EXPORTED)
        getConfigs()
    }
    override fun onPause(){
        super.onPause()
        unregisterReceiver(dataReceiver)
    }
    fun toggleSI(v: View?){
        val freedomUnits = findViewById<TextView>(R.id.txtTempUnit)
        val sharedPref =  getSharedPreferences("OPENPOT_PREFS",Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if(freedomUnits.text.equals("ºF")){
            freedomUnits.text = "ºC"
            editor.putBoolean("IS_CELSIUS", true)
        }
        else{
            freedomUnits.text = "ºF"
            editor.putBoolean("IS_CELSIUS", false)
        }
        editor.commit()
    }

    fun syncTime(v: View?){
        bleService.setTime()
    }
    fun getConfigs(){
        bleService.is24Hr()
        bleService.getTimer1()
        bleService.getTimer2()
    }
    fun timer1(v: View?){
        val txt = v as TextView
        val sharedPref = getSharedPreferences("OPENPOT_PREFS", Context.MODE_PRIVATE)
        val timer = sharedPref.getInt("TIMER1", 0)
        val hour = timer / 60
        val minutes = timer % 60
        val is24 = sharedPref.getBoolean("24", false)
        val picker = TimePickerDialog(
            this,
            { _, sHour, sMinute -> txt.text = "$sHour" + ":" +  String.format(Locale.US,"%02d", sMinute) }, hour, minutes, is24
        )
        picker.show()
    }
    fun timer2(v: View?){
        val txt = v as TextView
        val sharedPref = getSharedPreferences("OPENPOT_PREFS", Context.MODE_PRIVATE)
        val timer = sharedPref.getInt("TIMER2", 0)
        val hour = timer / 60
        val minutes = timer % 60
        val is24 = sharedPref.getBoolean("24", false)
        val picker = TimePickerDialog(
            this,
            { _, sHour, sMinute -> txt.text = "$sHour" + ":" + String.format(Locale.US,"%02d", sMinute) }, hour, minutes, is24
        )
        picker.show()
    }
    var dataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val sharedPref =  getSharedPreferences("OPENPOT_PREFS",Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            if (intent.action == "company.lizard.openpot.TWENTY_FOUR") {
                val data = intent.getByteArrayExtra("VALUE")
                val btn = findViewById<SwitchCompat>(R.id.btnMilTime)
                btn.isChecked = data!![0].toInt() == 0
                editor.putBoolean("24", data!![0].toInt() == 0)
            }
            else if(intent.action == "company.lizard.openpot.TIMER1"){
                val data = intent.getByteArrayExtra("VALUE")
                val timer = findViewById<EditText>(R.id.txtTimer1)
                editor.putInt("TIMER1", data!![0] * 60 + data!![1] % 60)
                val timerStr: String = data!![0].toString() + ":" + String.format(Locale.US, "%02d", data[1] % 60)
                timer.setText(timerStr)
            }
            else if(intent.action == "company.lizard.openpot.TIMER2"){
                val data = intent.getByteArrayExtra("VALUE")
                val timer = findViewById<EditText>(R.id.txtTimer2)
                editor.putInt("TIMER2", data!![0] * 60 + data!![1] % 60)
                val timerStr: String = data!![0].toString() + ":" + String.format(Locale.US,"%02d", data[1] % 60)
                timer.setText(timerStr)
            }
            editor.apply()
        }
    }
}
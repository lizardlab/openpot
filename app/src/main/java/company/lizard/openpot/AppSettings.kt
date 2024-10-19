package company.lizard.openpot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.state.ToggleableState
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class AppSettings : AppCompatActivity() {

    private lateinit var bleService: BLEService
    //val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    override fun onCreate(savedInstanceState: Bundle?) {
        //val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        bleService = BLEService(applicationContext)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pot_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val twentyFourToggle = findViewById<ToggleButton>(R.id.btnMilTime)
        twentyFourToggle.setOnCheckedChangeListener{ _, isChecked -> bleService.set24Hr(isChecked)}
    }

    override fun onResume() {
        super.onResume()
        val intFilter = IntentFilter("company.lizard.openpot.TWENTY_FOUR")
        intFilter.addAction("company.lizard.openpot.TIMER1")
        intFilter.addAction("company.lizard.openpot.TIMER2")
        ContextCompat.registerReceiver(this, dataReceiver, intFilter, ContextCompat.RECEIVER_EXPORTED)
    }
    fun toggleSI(v: View?){
        Log.d("G", "Toggle")
        val freedomUnits = findViewById<TextView>(R.id.txtTempUnit)
        if(freedomUnits.text.equals("ºF")){
            freedomUnits.text = "ºC"
        }
        else{
            freedomUnits.text = "ºF"
        }
    }
    fun syncTime(v: View?){
        bleService.setTime()
    }
    fun getConfigs(){
        bleService.is24Hr()
        bleService.getTimer1();
        bleService.getTimer2();
    }
    var dataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "company.lizard.openpot.TWENTY_FOUR") {
                val data = intent.getByteArrayExtra("VALUE")
                val btn = findViewById<ToggleButton>(R.id.btnMilTime)
                btn.isChecked = data!![0].toInt() == 0
            }
            else if(intent.action == "company.lizard.openpot.TIMER1"){

            }
        }
    }
}
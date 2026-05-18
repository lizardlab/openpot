package company.lizard.openpot

import android.util.Log

class Recipe(val method: Method = Method.PAUSE, val lcdMessage: LCDMessage = LCDMessage.NONE, val ledMode: LEDModes = LEDModes.NONE, val duration: Int = 0, val temperature: Int = 0, val heatingLevel: Int = 0, val sound: SoundBeep = SoundBeep.NONE,
    val music: Music = Music.NONE, val counter: Int) {
    enum class Method{
        HEAT_TEMP,
        HEAT_PERIOD,
        HEAT_PRESSURE,
        PAUSE,
        HOLD_PRESSURE,
        HOLD_TEMP
    }
    enum class LCDMessage(val bits: Int){
        NONE(0x0),
        DONE(0x1),
        ADD(0x2),
        YOGT(0x3),
        FOOD(0x4),
        HOT(0x5),
        ON(0x7),
        OFF(0x8),
        CNT_UP(0x9),
        CNT_DN(0xc)
    }
    enum class LEDModes(val bits: Int){
        NONE(0x0),
        MANUAL(0x20),
        KEEP_WARM(0x40)
    }
    enum class SoundBeep(val bits: Int){
        NONE(0x0),
        SHORT(0x1),
        LONG(0x2),
        SHORT_BEEP_15S(0x3)
    }
    enum class Music(val bits: Int){
        NONE(0x0),
        MUSIC1(0x1)
    }
    init {

    }
    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun bleString(): String {
        var bleCmd = "aa555a01" // preamble
        // UNKNOWN
        bleCmd = bleCmd.plus("4020") // first byte changes on some modes TODO
        bleCmd = bleCmd.plus("000000") // another unknown section
        // hours
        if(method == Method.PAUSE || method == Method.HOLD_TEMP){
            val hours = duration / 60
            bleCmd = bleCmd.plus(hours.toString(16))
            Log.d("TEST", "Hours: " + hours.toString(16))
        }
        else{
            bleCmd = bleCmd.plus("00")
            Log.d("TEST", "Hours padding")
        }
        // minutes
        if(method == Method.PAUSE || method == Method.HOLD_TEMP){
            val mins = duration % 60
            bleCmd = bleCmd.plus(mins.toString(16))
            Log.d("TEST", "Mins: " + mins.toString(16))
        }
        else{
            bleCmd = bleCmd.plus(duration.toString(16))
            Log.d("TEST", duration.toString(16))
        }
        bleCmd = bleCmd.plus((heatingLevel * 4).toString(16))
        Log.d("TEST", (heatingLevel * 4).toString(16))
        // status display
        val statusDisp = (ledMode.bits shl 5) or (lcdMessage.bits and 0x7)
        bleCmd = bleCmd.plus(statusDisp.toString(16))
        Log.d("TEST", statusDisp.toString(16))
        // sound selection
        val soundSelect = (sound.bits shl 7) or (music.bits shl 2)
        bleCmd = bleCmd.plus(soundSelect.toString(16))
        Log.d("TEST", soundSelect.toString(16))
        // priority selection
        if(method == Method.PAUSE || method == Method.HEAT_PERIOD){
            bleCmd = bleCmd.plus("00")
        }
        else if(method == Method.HEAT_PRESSURE || method == Method.HOLD_PRESSURE){
            bleCmd = bleCmd.plus("20")
        }
        else if(method == Method.HOLD_TEMP || method == Method.HEAT_TEMP){
            bleCmd = bleCmd.plus("40")
        }
        else{
            Log.d("TEST", method.name)
        }
        bleCmd = bleCmd.plus(counter.toString(16))

        return bleCmd
    }
}
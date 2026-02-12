package company.lizard.openpot

class Recipe {
    enum class METHOD{
        HEAT_TEMP,
        HEAT_PERIOD,
        HEAT_PRESSURE,
        PAUSE,
        HOLD_PRESSURE,
        HOLD_TEMP
    }
    enum class LCDMessage{
        NONE,
        DONE,
        ADD,
        YOGT,
        FOOD,
        HOT,
        ON,
        OFF,
        CNT_UP,
        CNT_DN
    }
    enum class LEDModes{
        NONE,
        MANUAL,
        KEEP_WARM
    }
    init {
        
    }
}
package dbClases

class MainWorkInfo(
    water: String,
    step: String,
    sleep: String,
    waterNorm: String,

) {

    private var water: String? = water
    private var step: String? = step
    private var sleep: String? = sleep
    private var waterNorm: String? = waterNorm



    fun setWater(Water: String) {
        this.water = Water
    }
    fun getWater(): String? {
        return water
    }

    fun setStep(Step: String) {
        this.step = Step
    }
    fun getStep(): String? {
        return step
    }

    fun setSleep(Sleep: String) {
        this.sleep = sleep
    }
    fun getSleep(): String? {
        return sleep
    }

    fun setWaterNorm(WaterNorm: String) {
        this.waterNorm = WaterNorm
    }
    fun getWaterNorm(): String? {
        return waterNorm
    }


}
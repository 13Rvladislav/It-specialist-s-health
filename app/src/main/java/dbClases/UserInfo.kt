package dbClases

class Users(
    name: String,
    height: String,
    weight: String,
    sex: String,
    workTime: String,
    lifeStyle: String
) {

    private var name: String? = name
    private var height: String? = height
    private var weight: String? = weight
    private var sex: String? = sex
    private var workTime: String? = workTime
    private var lifeStyle: String? = lifeStyle


    fun setName(Name: String) {
        this.name = Name
    }
    fun getName(): String? {
        return name
    }

    fun setHeight(Height: String) {
        this.height = Height
    }
    fun getHeight(): String? {
        return height
    }

    fun setWeight(Weight: String) {
        this.weight = Weight
    }
    fun getWeight(): String? {
        return weight
    }

    fun setSex(Sex: String) {
        this.sex = Sex
    }
    fun getSex(): String? {
        return sex
    }

    fun setWorkTime(WorkTime: String) {
        this.workTime = WorkTime
    }
    fun getWorkTime(): String? {
        return workTime
    }

    fun setLifeStyle(LifeStyle: String) {
        this.lifeStyle = LifeStyle
    }
    fun getLifeStyle(): String? {
        return lifeStyle
    }
}
package dbClases

class ToDos(
    nameTask: String,
    date: String
) {

    private var nameTask: String? = nameTask
    private var date: String? = date

    fun setNameTask(Name: String) {
        this.nameTask = Name
    }

    fun getNameTask(): String? {
        return nameTask
    }

    fun setDate(Date: String) {
        this.date = Date
    }

    fun getDate(): String? {
        return date
    }


}
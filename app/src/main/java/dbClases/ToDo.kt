package dbClases

class ToDo(
    name: String,
    date: String
) {

    private var name: String? = name
    private var date: String? = date

    fun setName(Name: String) {
        this.name = Name
    }

    fun getName(): String? {
        return name
    }

    fun setDate(Date: String) {
        this.date = Date
    }

    fun getDate(): String? {
        return date
    }


}
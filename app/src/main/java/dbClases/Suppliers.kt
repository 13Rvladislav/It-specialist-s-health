package dbClases

class Suppliers(name: String, coin: String, quantity: String, destroyed: String?) {

    private var name: String? = name

    private var coin: String? = coin
    private var quantity: String? = quantity
    private var destroyed: String? = destroyed

    class Suppliers constructor(
        name: String,
        type: String,
        coin: String,
        quantity: String,
        destroyed: String
    ) {

    }

    //
    fun setName(Name: String) {
        this.name = Name
    }

    fun getName(): String? {
        return name
    }

    //

    fun setCoin(Coin: String) {
        this.coin = Coin
    }

    fun getCoin(): String? {
        return coin
    }

    //
    fun setQuantity(Quantity: String) {
        this.quantity = Quantity
    }

    fun getQuantity(): String? {
        return quantity
    }

    //destroyed
    fun setDestroyed(Destroyed: String) {
        this.destroyed = Destroyed
    }

    fun getDestroyed(): String? {
        return destroyed
    }
}
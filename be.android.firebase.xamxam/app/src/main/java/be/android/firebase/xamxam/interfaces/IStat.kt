package be.android.firebase.xamxam.interfaces

interface IStat {
    val title:String
    val totalProducts:Int
    val minDate:String
    val maxDate:String

    fun areDatesEqual():Boolean = minDate == maxDate
}
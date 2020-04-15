package be.android.firebase.xamxam.interfaces

//interface used to implement common recyclerview functions
interface IBasicRecycle {
    fun makeRecyclerAdapter()

    fun notifyStorageChange(i:Int)

    fun notifyChanges()
}
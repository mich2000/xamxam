package be.android.firebase.xamxam.business

import be.android.firebase.xamxam.interfaces.IStat

/**
 * data class used to model the total of products and their max and min date, functionally
 * the same with StatisticsStorage. But differentiation will be on the type.
 * **/
data class StatisticsUser(override val title: String, val totalStorage:Int, override val totalProducts:Int,
    override val minDate:String, override val maxDate:String):IStat
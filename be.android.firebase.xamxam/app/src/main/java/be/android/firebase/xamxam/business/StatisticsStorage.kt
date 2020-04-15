package be.android.firebase.xamxam.business

import be.android.firebase.xamxam.interfaces.IStat

/**
 * data class used to model the total of products and their max and min date
 * **/
class StatisticsStorage(override val title: String, override val totalProducts:Int,
                        override val minDate:String, override val maxDate:String):IStat
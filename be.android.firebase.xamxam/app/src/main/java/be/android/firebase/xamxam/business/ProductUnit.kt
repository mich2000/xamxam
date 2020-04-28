package be.android.firebase.xamxam.business

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.classes.Converter.Companion.toLocalDate
import java.time.LocalDate
import java.util.concurrent.TimeUnit

//different categories for product units
val categories:Array<String> = arrayOf("vegetables","fruit", "grain","meat","fish","dairy",
"fat & sugar", "beans")

/**
 * on the basis of the category of a productunit it will return a drawable associated with that category
 *
 * link for free icons: https://www.flaticon.com/free-icon/carrot_2700373?term=vegetables&page=1&position=3
 *
 * link author for dairy icon: https://www.flaticon.com/authors/pixelmeetup
 * link author for sugar & fat, beans, meat, fish, fruit icon: https://www.flaticon.com/authors/freepik
 * link author for grain icon: https://www.flaticon.com/authors/pixelmeetup
 * link author for vegetable icon: href="https://www.flaticon.com/authors/wanicon
 * **/
fun returnCategoryIcon(context: Context, category: String):Drawable?{
    return ContextCompat.getDrawable(context, when(category){
        "vegetables" -> R.drawable.ic_vegetable
        "fruit"-> R.drawable.ic_fruit
        "grain"-> R.drawable.ic_grain
        "meat" -> R.drawable.ic_meat
        "fish"-> R.drawable.ic_fish
        "dairy" -> R.drawable.ic_dairy
        "fat & sugar" -> R.drawable.ic_sugar_fat
        "beans" -> R.drawable.ic_bean
        else -> null
    }!!)
}

/**
 * Function gives a number
 * 1: peremption date doesn't surpass the date of today
 * 0: peremption date equals the date of today
 * -1: peremption date is less than the date of today
 * **/
fun isOverDate(productUnit: ProductUnit):Int{
    val now = LocalDate.now()
    val datum = productUnit.bederfdatum!!.toLocalDate()
    return when {
        datum == now -> 0
        datum.isBefore(now) -> -1
        else -> 1
    }
}

    /**
     * class product representing the product
     *
     * link for pyramid view: https://en.wikipedia.org/wiki/Food_pyramid_(nutrition)
     * link for amount of days between 2 dates: https://stackoverflow.com/questions/42553017/android-calculate-days-between-two-dates
     * **/
    data class ProductUnit(var name:String? = null,
                           var hoeveelheid:Int? = null,
                           var category:String? = "",
                           var bederfdatum:String? = null) {

    /**
     * returns the amount of days between the peremption date and the date of today.
     * **/
    fun daysLeftOver():String {
        val daysLeft = (bederfdatum!!.toLocalDate().toEpochDay() - LocalDate.now().toEpochDay())
        return "${TimeUnit.DAYS.convert(daysLeft, TimeUnit.DAYS)} days left"
    }

    override fun toString(): String = "#$hoeveelheid - $name"
}
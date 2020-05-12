package be.android.firebase.xamxam.business

import be.android.firebase.xamxam.classes.Converter.Companion.toLocalDate
import be.android.firebase.xamxam.classes.Converter.Companion.toStringDate
import java.time.LocalDate

data class Storage(var name:String = "", var productUnitList:ArrayList<ProductUnit> = ArrayList()){
    /**
     * function used to sort the product on basis of its peremption date.
     * **/
    fun sortOnDate(){
        if(productUnitList.size != 0) productUnitList.sortBy { it.bederfdatum!!.toLocalDate() }
    }

    /**
     * returns the statistics about the storage itself, this means the amount of products and
     * minimum and maximum date of all the products.
     * **/
    fun statistics():StatisticsStorage{
        if(productUnitList.size == 0){
            return StatisticsStorage("Overview $name",0,"There are no products","There are no products")
        }
        var minDate: LocalDate = LocalDate.now()
        var maxDate: LocalDate = LocalDate.now()
        productUnitList.forEach { product ->
            val bederfDatum = product.bederfdatum!!.toLocalDate()
            if(bederfDatum.isBefore(minDate))minDate = bederfDatum
            if(bederfDatum.isAfter(maxDate)) maxDate = bederfDatum
        }
        return if(minDate == maxDate) StatisticsStorage("Overview storage $name",productUnitList.size,"Today","Today")
        else StatisticsStorage("Overview storage $name",productUnitList.size, minDate.toStringDate(),maxDate.toStringDate())
    }

    /**
     * function that returns a false if all products are good for consumption and false if their
     * perdition date is before or equal to to the date of today.
     * **/
    fun containsBadProducts():Boolean{
        if(productUnitList.size != 0){
            val now = LocalDate.now()
            productUnitList.forEach { product ->
                val date = product.bederfdatum!!.toLocalDate()
                if(date.isBefore(now) || date == now)
                    return true
            }
        }
        return false
    }

    /**
     * Returns the name and the amount of products that this storage has within
     * **/
    override fun toString(): String = "$name - ${productUnitList.size}"
}
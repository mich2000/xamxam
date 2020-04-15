package be.android.firebase.xamxam.business

import be.android.firebase.xamxam.classes.Converter.Companion.toStringDate
import be.android.firebase.xamxam.classes.Converter.Companion.toLocalDate
import java.time.LocalDate

data class XamUser(var storages:ArrayList<Storage> = ArrayList()){
    /**
     * Function made to control if the given name as parameter is unique within the list
     * **/
    private fun isStorageNameUnique(name:String):Boolean{
        for (storage in storages){
            if(name == storage.name){
                return false
            }
        }
        return true
    }

    /**
     * function to change the name of a indexed item and returns a boolean depenant if this works
     * **/
    fun changeStorageName(i:Int, newName:String):Boolean{
        if(isStorageNameUnique(newName)){
            storages[i].name = newName
            return true
        }
        return false
    }

    /**
     * function used to add storage
     * **/
    fun addStorage(storageName:String):Boolean{
        if(storageName == "")
            return false
        if(isStorageNameUnique(storageName)){
            storages.add(Storage(storageName))
            return true
        }
        return false
    }

    /**
     * function to remove a specific storage
     * **/
    fun removeStorage(index:Int):Boolean{
        storages.removeAt(index)
        return true
    }

    /**
     * function used to return all the names of the storages
     * **/
    fun allStorageNames():Array<String>{
        val storageNames:ArrayList<String> = ArrayList()
        for(storage in storages)
            storageNames.add(storage.name)
        return storageNames.toTypedArray()
    }

    /**
     * function to return the list of products that has been called by its storage name,
     * if none of the storage name equals to the parameter then null is returned
     * **/
    fun getProducts(storageName: String):ArrayList<ProductUnit>?{
        if(storages.size != 0){
            for (storage in storages){
                if(storage.name == storageName) return storage.productUnitList
            }
        }
        return null
    }

    fun getStorage(storageName: String):Storage?{
        if(storages.size != 0){
            for(storageIndex in 0..storages.size){
                if(storages[storageIndex].name == storageName) return storages[storageIndex]
            }
        }
        return null
    }

    fun getProductIndex(storage:String, productUnit:ProductUnit):Int?{
        val products = getProducts(storage)
        for(productIndex in 0..products!!.size){
            if(products[productIndex] == productUnit)
                return productIndex
        }
        return null
    }

    /**
     * adds a product to a storage, chosen by its name. returns a true if it managed to add
     * a product otherwhise it is false.
     * **/
    fun addProduct(storageName:String, productUnit:ProductUnit):Boolean {
        for (storage in storages){
            if(storage.name == storageName) {
                storage.productUnitList.add(productUnit)
                storage.sortOnDate()
                return true
            }
        }
        return false
    }

    /**
     * remove a product by index of an storage, chosen by its name. returns a true if it managed to add
     * a product otherwhise it is false.
     * **/
    fun removeProduct(storageName:String, index:Int):Boolean {
        for (storage in storages){
            if(storage.name == storageName) {
                storage.productUnitList.removeAt(index)
                return true
            }
        }
        return false
    }

    fun moveProduct(originalStorageName:String, targetStorageName:String, productUnit: ProductUnit){
        for(storage in storages){
            if(storage.name == originalStorageName){
                storage.productUnitList.remove(productUnit)
            }
            if(storage.name == targetStorageName){
                storage.productUnitList.add(productUnit)
                storage.sortOnDate()
            }
        }
    }

    /**
     * edits a product by index on a particular storage, chosen by its name. returns a true if
     * it managed to edit the product, otherwhise it is a false
     * **/
    fun editProduct(storageName:String, index:Int, productUnit:ProductUnit):Boolean {
        if(storages.size != 0){
            for (storage in storages){
                if(storage.name == storageName) {
                    storage.productUnitList[index] = productUnit
                    storage.sortOnDate()
                    return true
                }
            }
        }
        return false
    }

    /**
     * function used to return statistics about the user these include the amount of
     * products/storages and minimum, maximum of peremption date.
     * **/
    fun statistics(nameUser:String):StatisticsUser{
        if(storages.size == 0) return StatisticsUser("Statistics User",0, 0,
            "There are no storages and products","There are no storages and products")
        var sumProdcuts = 0
        var minDate:LocalDate = LocalDate.now()
        var maxDate:LocalDate = LocalDate.now()
        storages.filter { it.productUnitList.size != 0 }.forEach { storage ->
            sumProdcuts += storage.productUnitList.size
            storage.productUnitList.forEach { product ->
                val bederfDatum = product.bederfdatum!!.toLocalDate()
                if(bederfDatum.isBefore(minDate)) minDate = bederfDatum
                if(bederfDatum.isAfter(maxDate)) maxDate = bederfDatum
            }
        }
        return if(minDate == maxDate) StatisticsUser("Statistics $nameUser",storages.size,sumProdcuts,"Today","Today")
        else StatisticsUser("Statistics User",storages.size, sumProdcuts, minDate.toStringDate(),maxDate.toStringDate())
    }

    /**
     * function used to loop over each storage to see if it contains bad products, then it returns a
     * true and otherwise a false.
     * **/
    fun containsBadProducts():ArrayList<String>{
        val badStorages = ArrayList<String>()
        storages.filter { it.productUnitList.size != 0 }.forEach { storage ->
            if(storage.containsBadProducts())
                badStorages.add(storage.name)
        }
        return badStorages
    }
}
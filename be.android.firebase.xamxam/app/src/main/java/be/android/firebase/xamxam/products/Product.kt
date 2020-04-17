package be.android.firebase.xamxam.products


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.XamUser
import be.android.firebase.xamxam.classes.Handy
import be.android.firebase.xamxam.classes.ProductHandy
import be.android.firebase.xamxam.classes.StatisticsHandy
import be.android.firebase.xamxam.interfaces.IBasicRecycle
import be.android.firebase.xamxam.interfaces.IQuitable
import be.android.firebase.xamxam.storages.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_product.*

/**
 * A simple [Fragment] subclass.
 *
 * link backbutton callback: https://developer.android.com/guide/navigation/navigation-custom-back
 */
class Product : Fragment(),IQuitable,IBasicRecycle{
    //========================== VARIABLES ========================
    var storageName:String = ""
    set(value){
        if(value != field && value != ""){
            field = value
            updateAndUi()
            txtTitle.text = storageName
        }
    }

    private val auth = FirebaseAuth.getInstance()

    private val uidUser = auth.currentUser!!.uid

    private val users = FirebaseFirestore.getInstance().collection("users")

    private var docUser = users.document(uidUser)

    private lateinit var productAdapter: ProductAdapter

    //==================== LIFECYCLE CALLBACKS ================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            signOutDialog()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_product, container, false)

    /**
     * life cycle callback used to:
     * 1. used to update the recyclerview on with storage name that is given by the arguments
     * 2. sets up the menu
     * 3. set the onquerylistener to listen to changes in searchview text, to then filter
     * the recyclerview elemetns
     * **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getString("storage") != null && arguments != null){
            storageName = arguments?.getString("storage")!!
        }
        setHasOptionsMenu(true)
        searchNameProduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.constraint = newText!!
                return false
            }
        })
    }

    //=============================== FUNCTIONS ===================================
    /**
     * function to update the storages of the user, if it succeeds executes a Unit that the User
     * can chose to do. If it fails it will re-synchronize with the firestore
     * **/
    fun updateDocumentStorages(onSuccess:()->Unit = {}){
        if(context != null) {
            docUser.update("storages",Handy.xamXamUser!!.storages)
                .addOnSuccessListener {
                    onSuccess()
                    StorageService.enqueueWork(context, Intent())
                }
                .addOnFailureListener {
                    docUser.get().addOnSuccessListener { documentSnapshot ->
                        Handy.xamXamUser = documentSnapshot.toObject(XamUser::class.java)!!
                    }
                }
        }
    }

    /**
     * controls firstly if the user is equal to null or not. If null it will try to get all the
     * data from from firebase all again. After this it will initialise the recyclerview and
     * different callbacks on the add button and swipping of items for the CRUD operations.
     * **/
    private fun updateAndUi(){
        if(Handy.xamXamUser == null){
            docUser.get().addOnSuccessListener { documentSnapshot ->
                Handy.xamXamUser = documentSnapshot.toObject(XamUser::class.java)!!
            }
        }
        makeRecyclerAdapter()
        implementAddProductFabButton()
        setRecyclerViewItemTouchListener()
        touchProduct()
        setUpMovingProducts()
    }

    override fun signOut() = findNavController().navigate(R.id.productToStorage)

    override fun signOutDialog() {
        if(context != null) {
            Handy.yesNoDialog(context as Context,"Go to storages","Back to the storage page?", this::signOut)
        }
    }

    //========================== RECYCLERVIEW FUNCTIONS ========================
    /**
     * funtion used to setup the recyclerview.
     * **/
    override fun makeRecyclerAdapter() {
        if(storageName != "" && Handy.xamXamUser!!.getProducts(storageName) != null){
            productAdapter = ProductAdapter(storageName)
            RecyclerViewProduct.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.VERTICAL
                    adapter = productAdapter
                }
            }
            RecyclerViewProduct.addItemDecoration(basicItemDecorator())
        }
    }

    private fun touchProduct() {
        if(context != null && activity != null){
            productAdapter.clickEditProduct = { i: Int, _ ->
                val touchedProduct = productAdapter.filteredProductList[i]
                val user = Handy.xamXamUser!!
                val indexProduct = user.getProductIndex(storageName, touchedProduct)!!
                ProductHandy.productDialog(requireContext(),"Edit product", touchedProduct) {
                    if(user.editProduct(storageName, indexProduct, it)){
                        productAdapter.synchronize()
                        notifyStorageChange(i)
                        updateDocumentStorages {
                            Handy.snack(activity as FragmentActivity,"Product is succesfully edited in $storageName")
                        }
                    }
                }
            }
        }
    }

    /**
     * implements a function on the floating add button in the fragment, that will add the
     * add the product to the user and refresh the recyclerview.
     * **/
    private fun implementAddProductFabButton(){
        if(context != null && activity != null) {
            fabAddProduct.setOnClickListener {
                ProductHandy.productDialog(requireContext(),"Add product") {
                    val product = it
                    if(product != null){
                        if(Handy.xamXamUser!!.addProduct(storageName, product)){
                            updateDocumentStorages {
                                Handy.snack(requireActivity(),"Product added to $storageName")
                                productAdapter.synchronize()
                            }
                        }
                    }else{
                        Handy.toast(requireActivity(),requireContext(),"product is leeg",true)
                    }
                }
            }
        }
    }

    /**
     * function used to implement deleting products by swiping the appropiate view item
     * **/
    private fun setRecyclerViewItemTouchListener(){
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                viewHolder1: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val user = Handy.xamXamUser!!
                val swipedProductIndex = user.getProductIndex(storageName,
                    productAdapter.filteredProductList[viewHolder.adapterPosition])
                if(user.removeProduct(storageName, swipedProductIndex!!)){
                    updateDocumentStorages {
                        Handy.snack(activity!!,"Product deleted from $storageName")
                        productAdapter.synchronize()
                        notifyStorageChange(viewHolder.adapterPosition)
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(RecyclerViewProduct)
    }

    private fun setUpMovingProducts(){
        if(activity != null && context != null) {
            productAdapter.longClickMoveProduct = { pos:Int,view:View ->
                val user = Handy.xamXamUser!!
                val productIndex = user.getProductIndex(storageName,
                    productAdapter.filteredProductList[pos])
                Handy.arrayDialog(requireContext(), "Move to storages",Handy.xamXamUser!!.allStorageNames(),
                    { chosenItemString: String, chosenItemIndex:Int ->
                        try {
                            user.moveProduct(storageName, chosenItemString, user.getStorage(storageName)!!
                                .productUnitList[productIndex!!])
                            updateDocumentStorages {
                                Handy.toast(requireActivity(),requireContext(),"Product has been moved")
                                productAdapter.synchronize()
                            }
                        }catch (e:Exception){
                            Handy.toast(requireActivity(),requireContext(), "An error happened, " +
                                    "the product has not been moved")
                        }
                    }, "Ok")
            }
        }
    }

    /**
     * Returns a item seperator for between recyclerview elements, takes the UI from a self-made
     * drawable XML-element
     * **/
    private fun basicItemDecorator(): RecyclerView.ItemDecoration{
        return DividerItemDecoration(context, DividerItemDecoration.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        }
    }

    /**
     * Function used to notify changes of the data of a particular item
     * **/
    override fun notifyStorageChange(i: Int) {
        if(activity != null)
            Handy.ui(requireActivity()) { productAdapter.notifyItemChanged(i) }
    }

    override fun notifyChanges() {
        if(activity != null)
        Handy.ui(requireActivity()) { productAdapter.notifyDataSetChanged() }
    }

    //========================== MENU FUNCTIONS ========================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.product_menu, menu)
    }

    /**
     * Function used to give actions to each
     * **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.Statistics -> {
                StatisticsHandy.showStatistics(requireContext(),
                    Handy.xamXamUser!!.getStorage(storageName)!!.statistics())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
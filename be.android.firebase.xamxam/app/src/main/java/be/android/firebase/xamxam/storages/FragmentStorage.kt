package be.android.firebase.xamxam.storages

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.Storage
import be.android.firebase.xamxam.business.XamUser
import be.android.firebase.xamxam.classes.Handy
import be.android.firebase.xamxam.classes.StatisticsHandy
import be.android.firebase.xamxam.interfaces.IBasicRecycle
import be.android.firebase.xamxam.interfaces.IQuitable
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_storage.*

/**
 * A simple [Fragment] subclass.
 *
 * fragment used for CRUD operation on the storages of a user
 *
 * link used for firestore: https://firebase.google.com/docs/firestore/query-data/get-data
 * link used for adding/removing elements from array in firestore: https://firebase.google.com/docs/firestore/manage-data/add-data
 * link for UI of the storage fragment: https://stackoverflow.com/questions/47876313/right-and-bottom-constraint-on-floatingactionbutton-makes-it-unseen
 * link for bundles for between destination: https://developer.andr=oid.com/guide/navigation/navigation-pass-data
 */
class FragmentStorage : Fragment(), IQuitable, IBasicRecycle{
    //=============== VARIABLES ================
    private lateinit var auth : FirebaseAuth
    private lateinit var uidUser : String
    private lateinit var users : CollectionReference
    private lateinit var docUser : DocumentReference
    private lateinit var storageAdapter: StorageAdapter

    //==================== LIFECYCLE CALLBACKS ====================
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            : View = inflater.inflate(R.layout.fragment_storage, container, false)

    /**
     * callback function used to instantiate/
     * the recyclerview and its respective callbacks for CRUD operations
     * the title will personalised for each user.
     * will instantiate the options
     * will see if any bad products are found to notify the user of it.
     * **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        uidUser = auth.currentUser!!.uid
        users = FirebaseFirestore.getInstance().collection("users")
        docUser = users.document(uidUser)
        updateUserAndUi()
        txtTitle.text = "Storages"
        requireActivity().onBackPressedDispatcher.addCallback(this){
            signOutDialog()
        }
        setHasOptionsMenu(true)
    }

    //==================== FUNCTIONS ====================
    /**
     * function used to edit the storage name of a storage item you have touched, a dialog
     * appears and changes can be done through it. then after the user confirms the edit
     * the document is updated and a snackbar will apear saying you updated it
     * successfully.
     * **/
    private fun touchStorage(){
        storageAdapter.storageShowLongClick = { i: Int, _ ->
            val storageName = Handy.xamXamUser!!.storages[i].name
            Handy.inputDialog(requireActivity(),"Edit storage name",storageName) {
                if(Handy.xamXamUser!!.changeStorageName(i, it)){
                    updateDocumentStorages {
                        Handy.snack(requireActivity(),"Storage has been edited: $storageName to $it", true)
                        notifyStorageChange(i)
                    }
                }
            }
        }
    }

    /**
     * Implements a Unit that when the button is clicked will redirect you to the product fragment
     * and this one will show all the products of that specific storage where you pushed on the button
     * **/
    private fun implementSeeProducts(){
        storageAdapter.storageEditClick = { i, _ ->
            findNavController().navigate(R.id.StorageToProducts,
                bundleOf("storage" to Handy.xamXamUser!!.storages[i].name))
        }
    }

    /**
     * Function overview in steps:
     * 1. Function used to fill xamXamUser with the content of the document linked to the uid
     * of that user.
     * 2. If the step above succeeds and that the user variable is not empty then
     * **/
    private fun updateUserAndUi(){
        if(Handy.xamXamUser == null){
            docUser.get().addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot != null  ) {
                        val user = documentSnapshot.toObject(XamUser::class.java)
                        if (user != null) {
                            Handy.xamXamUser = user
                            Handy.editTitle(requireActivity(),auth.currentUser!!.displayName!!)
                            makeRecyclerAdapter()
                            implementAddStorageFABbtn()
                            setRecyclerViewItemTouchListener()
                            implementSeeProducts()
                            StorageService.enqueueWork(requireContext(), Intent())
                        }
                    }
                }.addOnFailureListener {
                    Handy.toast(requireActivity(),requireContext(),"Auth failed")
                }
        }else{
            Handy.editTitle(requireActivity(),auth.currentUser!!.displayName!!)
            makeRecyclerAdapter()
            implementAddStorageFABbtn()
            setRecyclerViewItemTouchListener()
            implementSeeProducts()
            StorageService.enqueueWork(requireContext(), Intent())
        }
    }

    override fun signOut(){
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
                findNavController().navigate(R.id.toAuthFragment)
            }.addOnFailureListener {
                Handy.toast(requireActivity(), requireContext(), "An error occured when logging out")
            }
    }

    override fun signOutDialog(){
        Handy.yesNoDialog(requireContext(),"Do you wish to log out?",{signOut()})
    }

    //function to update the storages of the user
    private fun updateDocumentStorages(onSuccess:()->Unit = {}){
        docUser.update("storages",Handy.xamXamUser!!.storages)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                docUser.get().addOnSuccessListener { documentSnapshot ->
                    Handy.xamXamUser = documentSnapshot.toObject(XamUser::class.java)!!
                }
            }
    }

    /**
     * Implements an action to the clicklistener of the add button, to add storages
     * and to update the document linked with the user
     *
     * warning: when adding items to the array these must match because data
     * is serialised at the start
     * **/
    private fun implementAddStorageFABbtn(){
        fabAddStorage.setOnClickListener {
            Handy.inputDialog(requireActivity(),"Add storage", "") {
                val name = it
                docUser.update("storages", FieldValue.arrayUnion(Storage(name)))
                    .addOnSuccessListener {
                        if(Handy.xamXamUser!!.addStorage(name)){
                            Handy.snack(requireActivity(),"Storage $name is added")
                            notifyChanges()
                    }
                }.addOnFailureListener {
                    docUser.get().addOnSuccessListener { documentSnapshot ->
                        Handy.xamXamUser = documentSnapshot.toObject(XamUser::class.java)!!
                    }
                }
            }
        }
    }

    //===================== RECYCLERVIEW FUNCTIONS ===================
    /**
     * Function used to implement the recyclerview and all its events that could through it
     * ex. clicking on item, swipting, ... only if the xamUser variable is not equal to null.
     * List of the recyclerview will be based of the
     * **/
    override fun makeRecyclerAdapter(){
        if(Handy.xamXamUser != null && context != null){
            storageAdapter = StorageAdapter(Handy.xamXamUser!!.storages)
            RecyclerViewStorage.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = storageAdapter
            }
            touchStorage()
            RecyclerViewStorage.addItemDecoration(basicItemDecorator())
        }
    }

    /**
     * Implements a swipe(left or right) so that storages can be deleted of the storages list of
     * an user. this updates the document linked to the user and notifies the changes to
     * the adapter of the storage list
     * **/
    private fun setRecyclerViewItemTouchListener(){
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                viewHolder1: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val storage = Handy.xamXamUser!!.storages[viewHolder.adapterPosition]
                Handy.yesNoDialog(requireContext(),"Remove the storage ${storage.name}?",{
                    docUser.update("storages", FieldValue.arrayRemove(storage))
                    if(Handy.xamXamUser!!.removeStorage(viewHolder.adapterPosition)){
                        Handy.snack(requireActivity(),"Storage ${storage.name} is deleted")
                        notifyChanges()
                        StorageService.enqueueWork(requireContext(), Intent())
                    }
                },{
                    notifyChanges()
                })
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(RecyclerViewStorage)
    }

    private fun basicItemDecorator(): RecyclerView.ItemDecoration{
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).also {
            it.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        }
    }

    /**
     * Notifies the changes that happen within the list to the recyclerview, this is only for
     * items that are added or removed, not modified.
     * **/
    override fun notifyChanges(){
        Handy.ui(requireActivity()) { storageAdapter.notifyDataSetChanged() }
    }

    /**
     * Notifies a specific item within the storage list of the user, only for items
     * who has changed.
     * **/
    override fun notifyStorageChange(i:Int){
        Handy.ui(requireActivity()) { storageAdapter.notifyItemChanged(i) }
    }

    //==================== MENU FUNCTIONS ====================
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.storage_menu, menu)
    }

    /**
     * Function used to give actions to each
     * **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.Statistics -> {
                StatisticsHandy.showStatistics(requireContext(),
                    Handy.xamXamUser!!.statistics()
                )
                true
            }
            R.id.Profile -> {
                findNavController().navigate(R.id.storageToProfile)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
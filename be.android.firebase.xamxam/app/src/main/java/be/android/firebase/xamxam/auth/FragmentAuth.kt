package be.android.firebase.xamxam.auth


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.classes.Handy
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_auth.*


/**
 * A simple [Fragment] subclass.
 *
 * Class has been greatly based on the basic firebaseUI class that is in the firebase tutorial
 * and README.md of the github project where it was based on.
 * link: https://firebase.google.com/docs/auth/android/firebaseui?authuser=0
 * link README.md: https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
 * link: https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase/56272319
 * **/
class FragmentAuth : Fragment(){
    //========================= VARIABLES ========================
    companion object {
        const val RC_SIGN_IN = 123
    }

    private var auth:FirebaseAuth = FirebaseAuth.getInstance()

    //========================== LIFECYCLE CALLBACKS ===========================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controlUserAuth()
        btnSignIn.setOnClickListener { createSignInIntent() }
    }

    //========================== FUNCTIONS ===========================
    /**
     * Function that decides what happens when the user starts the app and controls if there is
     * already a user logged in.
     *
     * If a user is logged in then he is redirected to the storage fragment
     * **/
    private fun controlUserAuth(){
        if (auth.currentUser != null) {
            val user = auth.currentUser!!.displayName
            Handy.editTitle(requireActivity(),user!!)
            findNavController().navigate(R.id.toStorageFragment)
        } else {
            Handy.editTitle(requireActivity())
        }
    }

    /**
     * Function used to start the sign-in process for the email and google account.
     *
     * An firebase UI will show itself to the user for sign-in and registration.
     *
     * login/registration: Gmail & Email authenthication
     * **/
    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN)
    }

    /**
     * Function to decide what happens with authentication result. If the authentication succeeds
     * the user is redirected to another fragment, if not the use will get a toast saying that it
     * failed.
     * **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response:IdpResponse? = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK && response != null) {
                val user = auth.currentUser!!.displayName!!
                Handy.editTitle(requireActivity(),user)
                Handy.xamXamUser = null
                findNavController().navigate(R.id.toStorageFragment)
            } else {
                Handy.toast(requireActivity(),requireContext(),"Auth failed",true)
            }
        }
    }
}
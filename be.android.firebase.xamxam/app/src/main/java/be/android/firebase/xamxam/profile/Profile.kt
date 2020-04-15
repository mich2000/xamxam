package be.android.firebase.xamxam.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.android.firebase.xamxam.MainActivity
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.classes.Handy
import be.android.firebase.xamxam.interfaces.IQuitable
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass
 *
 * link used for modifying a user profile: https://firebase.google.com/docs/auth/android/manage-users
 * link used for a normal black line: https://stackoverflow.com/questions/5049852/android-drawing-separator-divider-line-in-layout
 * link used to detect which sign in method it was: https://stackoverflow.com/questions/38619628/how-to-determine-if-a-firebase-user-is-signed-in-using-facebook-authentication/46014063#46014063
 */
class Profile : Fragment(), IQuitable {
    //================= VARIABLES ===================
    private lateinit var auth: FirebaseAuth

    private lateinit var user : FirebaseUser

    //===================== LIFECYCLE FUNCTIONS ==================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            signOutDialog()
        }
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!user.isEmailVerified){
            Handy.toast(activity!!,context!!, "To view your profile you have to confirm you're mail",true)
            Handy.yesNoDialog(context!!, "Send confirmation mail?",
                {
                    sendConfirmMail()
                    findNavController().navigate(R.id.profileToStorage)
                },
                {
                    findNavController().navigate(R.id.profileToStorage)
                })
        } else {
            updateUI()
            btnChangeName.setOnClickListener { changeName() }
            btnChangePassword.setOnClickListener { updatePassword() }
            btnDeleteAccount.setOnClickListener { deleteProfile() }
        }
    }

    //================= FUNCTIONS ====================
    override fun signOut() = findNavController().navigate(R.id.profileToStorage)

    override fun signOutDialog() = Handy.yesNoDialog(context!!,"Go to storages"
        , "Back to the storage page?", this::signOut)

    private fun updateUI(){
        Handy.editTitle(activity!!, user.displayName!!)
        nameText.text = user.displayName!!
        emailText.text = user.email
        emailVerifiedText.text =
            if(!user.isEmailVerified) "Email has not been verified"
            else "Email has been verified"
    }

    /**
     * function used to show a dialog to change the name of the user.
     * **/
    private fun changeName(){
        Handy.inputDialog(context!!, "Do you really want to change you're name?", user.displayName!!)
        {
            name -> updateName(name)
        }
    }

    /**
     * function used to update the name of the user. if the user changes his name, a snackbar will be
     * shown asking the user if he will revert to his original name.
     * **/
    private fun updateName(name:String){
        val originalName = user.displayName!!
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Handy.toast(activity!!,context!!, "Name has successfully been changed",true)
                    updateUI()
                    Handy.snack(activity!!, "Do you want to change you're name back?", true) {
                        updateName(originalName)
                    }
                } else {
                    Handy.toast(activity!!,context!!, "An error happened, name could not be changed",true)
                }
            }.addOnFailureListener {
                Handy.toast(activity!!,context!!, "An error happened, name could not be changed", true)
            }
    }

    /**
     * function used to update the password. you need to be reauthenticate to be able to do this.
     * **/
    private fun updatePassword(){
        if(user.isEmailVerified){
            reauthenticate {
                Handy.passwordConfirmDialog(activity!!,context!!,"Set your new password") {
                    user.updatePassword(it)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Handy.toast(activity!!,context!!, "Password has been changed")
                            } else {
                                Handy.toast(activity!!,context!!, "Password has failed to be changed")
                            }
                        }
                }
            }
        } else {
            sendConfirmMail()
        }
    }

    /**
     * function used to delete the profile of the current user. it will also delete the user doc in the
     * firebase firestore.
     * **/
    private fun deleteProfile(){
        reauthenticate {
            Handy.yesNoDialog(context!!, "Do you really want to delete you're profile?", {
                user.delete()
                    .addOnSuccessListener {
                        Handy.toast(activity!!,context!!, "You're profile has successfully been deleted.")
                        startActivity(Intent(context!!,MainActivity::class.java))
                    }.addOnFailureListener {
                        Handy.toast(activity!!,context!!, "You're profile couldn't be deleted, it didn't succeed.")
                    }
            })
        }
    }

    /**
     * function used to send a mail confirmation
     * **/
    private fun sendConfirmMail(){
        Handy.yesNoDialog(context!!, "Do you want to send a confirmation mail") {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    Handy.toast(activity!!,context!!, "A confirmation email has been send",true)
                }.addOnFailureListener {
                    Handy.toast(activity!!,context!!, "Could not send a confirmation email",true)
                }
        }
    }

    /**
     * function used to reauthenticate the user to be able to perform more advanced features for
     * accounts
     * **/
    private fun reauthenticate(OnSuccess:() -> Unit){
        for (UserInfo in FirebaseAuth.getInstance().currentUser!!.providerData) {
            when (UserInfo.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    Handy.passwordDialog(context!!, "Please put you're password: ") {
                        user.reauthenticate(EmailAuthProvider.getCredential(user.email!!,it))
                            .addOnSuccessListener {
                                Handy.toast(activity!!,context!!,"You have been reauthenticated")
                                OnSuccess()
                            }.addOnFailureListener {
                                Handy.toast(activity!!,context!!, "Reauthentication failed")
                            }
                    }
                }
                GoogleAuthProvider.PROVIDER_ID -> {
                    Handy.passwordDialog(context!!, "Please put you're password: ") {
                        user.reauthenticate(GoogleAuthProvider.getCredential(user.email!!,it))
                            .addOnSuccessListener {
                                Handy.toast(activity!!,context!!,"You have been reauthenticated")
                                OnSuccess()
                            }.addOnFailureListener {
                                Handy.toast(activity!!,context!!, "Reauthentication failed")
                            }
                    }
                }
            }
        }
    }
}
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
class Profile : Fragment() {
    //================= VARIABLES ===================
    private lateinit var auth: FirebaseAuth

    private lateinit var user : FirebaseUser

    //===================== LIFECYCLE FUNCTIONS ==================
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        if(!user.isEmailVerified){
            Handy.toast(requireActivity(),requireContext(), "To view your profile you have to confirm you're mail",true)
            Handy.yesNoDialog(requireContext(), "Send confirmation mail?",
                {
                    sendConfirmMail()
                })
            findNavController().navigate(R.id.profileToStorage)
        } else {
            updateUI()
            btnChangeName.setOnClickListener { changeName() }
            btnChangePassword.setOnClickListener { updatePassword() }
            btnDeleteAccount.setOnClickListener { deleteProfile() }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigate(R.id.profileToStorage)
        }
    }

    //================= FUNCTIONS ====================
    private fun updateUI(){
        Handy.editTitle(requireActivity(), user.displayName!!)
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
        Handy.inputDialog(requireContext(), "Do you really want to change you're name?", user.displayName!!)
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
                    Handy.toast(requireActivity(),requireContext(), "Name has successfully been changed",true)
                    updateUI()
                    Handy.snack(requireActivity(), "Do you want to change you're name back?", true) {
                        updateName(originalName)
                    }
                } else {
                    Handy.toast(requireActivity(),requireContext(), "An error happened, name could not be changed",true)
                }
            }.addOnFailureListener {
                Handy.toast(requireActivity(),requireContext(), "An error happened, name could not be changed", true)
            }
    }

    /**
     * function used to update the password. you need to be reauthenticate to be able to do this.
     * **/
    private fun updatePassword(){
        if(user.isEmailVerified){
            reauthenticate {
                Handy.passwordConfirmDialog(requireActivity(),requireContext(),"Set your new password") {
                    user.updatePassword(it)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Handy.toast(requireActivity(),requireContext(), "Password has been changed")
                            } else {
                                Handy.toast(requireActivity(),requireContext(), "Password has failed to be changed")
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
            Handy.yesNoDialog(requireContext(), "Do you really want to delete you're profile?", {
                user.delete()
                    .addOnSuccessListener {
                        Handy.toast(requireActivity(),requireContext(), "You're profile has successfully been deleted.")
                        startActivity(Intent(requireContext(),MainActivity::class.java))
                    }.addOnFailureListener {
                        Handy.toast(requireActivity(),requireContext(), "You're profile couldn't be deleted, it didn't succeed.")
                    }
            })
        }
    }

    /**
     * function used to send a mail confirmation
     * **/
    private fun sendConfirmMail(){
        Handy.yesNoDialog(requireContext(), "Do you want to send a confirmation mail") {
            user.sendEmailVerification()
                .addOnSuccessListener {
                    Handy.toast(requireActivity(),requireContext(), "A confirmation email has been send",true)
                }.addOnFailureListener {
                    Handy.toast(requireActivity(),requireContext(), "Could not send a confirmation email",true)
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
                    Handy.passwordDialog(requireContext(), "Please put you're password: ") {
                        user.reauthenticate(EmailAuthProvider.getCredential(user.email!!,it))
                            .addOnSuccessListener {
                                Handy.toast(requireActivity(),requireContext(),"You have been reauthenticated")
                                OnSuccess()
                            }.addOnFailureListener {
                                Handy.toast(requireActivity(),requireContext(), "Reauthentication failed")
                            }
                    }
                }
                GoogleAuthProvider.PROVIDER_ID -> {
                    Handy.passwordDialog(requireContext(), "Please put you're password: ") {
                        user.reauthenticate(GoogleAuthProvider.getCredential(user.email!!,it))
                            .addOnSuccessListener {
                                Handy.toast(requireActivity(),requireContext(),"You have been reauthenticated")
                                OnSuccess()
                            }.addOnFailureListener {
                                Handy.toast(requireActivity(),requireContext(), "Reauthentication failed")
                            }
                    }
                }
            }
        }
    }
}
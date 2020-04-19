package be.android.firebase.xamxam.classes

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import be.android.firebase.xamxam.R
import be.android.firebase.xamxam.business.XamUser
import com.google.android.material.snackbar.Snackbar


/**
 * Static class used for functions used through out the application and preferably in fragments
 *
 * link for alertdialog: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
 * link for returning values from a custom dialog: https://stackoverflow.com/questions/36651655/returning-a-value-from-alertdialog
 * link for snackbar: https://developer.android.com/training/snackbar/showing
 * link for DatePicker and LocalDate? type: https://www.geeksforgeeks.org/datepicker-in-kotlin/
 * link for spinner: https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
 * link dialog with multiple items: https://www.journaldev.com/309/android-alert-dialog-using-kotlin
 * link used so that password can't be seen: https://stackoverflow.com/questions/25674711/android-how-to-hide-text-being-entered-in-edit-text-box
 * **/
class Handy {
    companion object{
        //==================== VARIABLES ======================
        var xamXamUser: XamUser? = null

        //===================== UI FUNCTION ====================
        //function used to run a Unit inside the UiThread
        fun ui(activity: Activity, unit:() -> Unit ) = activity.runOnUiThread(unit)

        // ====================== TOAST FUNCTION =============
        /**
         * function used to show any object, because they will be called with toString().
         * **/
        fun toast(activity: Activity, context: Context,msg: Any, long:Boolean = false) {
            ui(activity) {
                Toast.makeText(context,
                        msg.toString(),
                        if(long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //================= APPBAR FUNCTIONS ==========
        /**
         * Functions used to change the app name
         * newTitle => if empty the actionbar title will be named with the orignal string that was
         * there
         * **/
        fun editTitle(activity: Activity, newTitle:String = ""){
            if(newTitle != "")
                activity.title = newTitle
            else
                activity.title = activity.getString(R.string.app_name)
        }

        //================================ DIALOG FUNCTIONS ==============================
        /**
         * Dialog function that show a dialog will ask a yes or no from the user. if it the answer
         * is yes then the action will be executed. if it is no then nothing will happen.
         * title => title showed on the alertdialog
         * msg => message showed on the alertdialog in the middle
         * yesAction => function executed when user choses yes
         * noAction => function executed when user choses no
         *
         * link dialogListener: https://stackoverflow.com/questions/16120697/kotlin-how-to-pass-a-function-as-parameter-to-another
         * **/
        fun yesNoDialog(context: Context, title:String, msg:String, yesAction:() -> Unit = {}, noAction:() -> Unit = {}){
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Yes") { dialog:DialogInterface, _:Int ->
                    yesAction()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog:DialogInterface, _:Int ->
                    noAction()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        fun yesNoDialog(context: Context, title:String, yesAction:() -> Unit = {}, noAction:() -> Unit = {}){
            AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("Yes") { dialog:DialogInterface, _:Int ->
                    yesAction()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog:DialogInterface, _:Int ->
                    noAction()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        /**
         * Dialog window used to get input from users and then through a Unit(void) to
         * work with that input. When a developper wants to use the string parameter of the
         * Unit he needs to use "it"
         * **/
        fun inputDialog(context: Context, title:String, InitialInput:String, answer:(InitialInput:String) -> Unit){
            val input = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_TEXT
                setText(InitialInput)
            }
            AlertDialog.Builder(context)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Yes") { dialog:DialogInterface, _ ->
                    answer(input.text.toString())
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog:DialogInterface, _ ->
                    dialog.dismiss()
                }.create()
                .show()
        }

        fun passwordDialog(context: Context, title:String, answer:(InitialInput:String) -> Unit){
            val password = EditText(context).apply {
                transformationMethod = PasswordTransformationMethod.getInstance()
                hint = "Password"
            }
            AlertDialog.Builder(context)
                .setTitle(title)
                .setView(password)
                .setPositiveButton("Yes") { dialog:DialogInterface, _ ->
                    answer(password.text.toString())
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog:DialogInterface, _ ->
                    dialog.dismiss()
                }.create()
                .show()
        }

        fun passwordConfirmDialog(activity: Activity, context: Context, title:String, answer:(InitialInput:String) -> Unit){
            val password = EditText(context).apply {
                transformationMethod = PasswordTransformationMethod.getInstance()
                hint = "Password"
            }
            val confirmPassword = EditText(context).apply {
                transformationMethod = PasswordTransformationMethod.getInstance()
                hint = "Confirm password"
            }
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            layout.addView(password)
            layout.addView(confirmPassword)
            AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton("Yes") { dialog:DialogInterface, _ ->
                    if(password.text.toString() == confirmPassword.text.toString()){
                        answer(password.text.toString())
                        dialog.dismiss()
                    } else {
                        toast(activity,context,"Password and confirm password are not the same", true)
                    }
                }.setNegativeButton("No") { dialog:DialogInterface, _ ->
                    dialog.dismiss()
                }.create()
                .show()
        }

        /**
         * function used to show a dialog where the user can chose over a range of items and has
         * the choice to click on one of these items. this will trigger a Unit with the string
         * of the represented item or its index.
         * **/
        fun arrayDialog(context: Context, title: String, arrayItems:Array<String>,
                        answer: (chosenItemString: String, chosenItemIndex:Int) -> Unit, dismissBtnText:String = "Dismiss"){
            AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(arrayItems) { _: DialogInterface?, which: Int ->
                    answer(arrayItems[which], which)
                }
                .setNeutralButton(dismissBtnText) { dialog:DialogInterface, _ ->
                    dialog.dismiss()
                }.create()
                .show()
        }

        //======================== SNACKBAR FUNCTIONS =======================
        /**
         * Shows a basic snackbar at the bottom and shows a message of a chosen amount of
         * time.
         * **/
        fun snack(activity: Activity, message:Any, long:Boolean = false, action:() -> Unit = {}) =
            Snackbar.make(activity.findViewById(android.R.id.content),
                message.toString(),
                if(long) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT)
                .setAction("Ok") { action() }
                .show()

    }
}
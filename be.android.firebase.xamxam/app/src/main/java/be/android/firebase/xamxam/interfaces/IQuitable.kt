package be.android.firebase.xamxam.interfaces

interface IQuitable {
    /**
     * function to signout of firebase auth and to go the login/register fragment
     * **/
    fun signOut()

    /**
     * Gives you the choice to quit or not and then to exeucte Signout
     * **/
    fun signOutDialog()
}
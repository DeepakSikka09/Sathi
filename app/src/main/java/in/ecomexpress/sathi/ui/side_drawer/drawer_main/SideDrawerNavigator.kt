package `in`.ecomexpress.sathi.ui.side_drawer.drawer_main

interface SideDrawerNavigator {

    fun showMessageOnUI(message: String?, isError: Boolean)

    fun clearStackAndMoveToLoginActivity(clearPreferences : Boolean)
}
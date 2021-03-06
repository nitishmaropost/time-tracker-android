package com.maropost.management.commons.application

import android.app.Application
import android.text.TextUtils
import com.maropost.management.R
import com.maropost.management.commons.utils.SharedPreferenceHelper
import com.maropost.management.commons.activities.MainActivity

class MyApplication : Application() {

    private lateinit var mainActivity: MainActivity
    var accessToken = ""
    var user_type= USER_TYPE.EMPLOYEE

    /**
     * Enum user types
     */
    enum class USER_TYPE {
        EMPLOYEE,
        ADMIN
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getInstance(): MyApplication {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (instance == null)
            instance = this
        checkIfLoginTokenAvailable()
        checkForUserType()
    }


    fun setMainActivityInstance(actiity: MainActivity) {
        this.mainActivity = actiity
    }

    /**
     * Check if login token is present in preference or not
     * If present assign it in Login Model which will be used as cache till the time app is being viewed.
     * No need to access it time and again from preference.
     */
    private fun checkIfLoginTokenAvailable() {
        val prefernceToken = SharedPreferenceHelper.getInstance().getSharedPreference(
            this,
            getString(R.string.login_token),
            SharedPreferenceHelper.PreferenceDataType.STRING
        ) as String
        if (!TextUtils.isEmpty(prefernceToken))
            accessToken = prefernceToken
    }

    /**
     * Check if user role is present in preference or not
     * If present assign it in Login Model which will be used as cache till the time app is being viewed.
     * No need to access it time and again from preference.
     */
    fun checkForUserType() {
        val userRole = SharedPreferenceHelper.getInstance().getSharedPreference(
            this, getString(R.string.user_role), SharedPreferenceHelper.PreferenceDataType.STRING) as String
        if (!TextUtils.isEmpty(userRole)){
            when(userRole){
                "employee" -> user_type = USER_TYPE.EMPLOYEE
                "admin"    -> user_type = USER_TYPE.ADMIN
            }
        }
    }

    fun navigationItemTapped(menuItem: String){
       mainActivity.checkMenuItemTapped(menuItem)
    }

    fun onQueryTextChange(newText: String) {
        mainActivity.onQueryTextChange(newText)
    }
}
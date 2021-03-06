package com.maropost.management.commons.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.Button
import com.maropost.management.R
import com.maropost.management.commons.application.MyApplication
import com.maropost.management.time.pojomodels.NavigationItem
import com.maropost.management.time.view.adapters.NavigationAdapter
import com.maropost.management.time.view.adapters.NavigationAdapterCallbacks
import com.maropost.management.time.view.fragments.HomeFragment
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.menu_left_drawer.*
import kotlin.collections.ArrayList


open class MPBaseActivity : AppCompatActivity(), NavigationAdapterCallbacks {

    private var slidingRootNav: SlidingRootNav ?= null
    private var navigationAdapter:NavigationAdapter ? = null
    private var itemList  = ArrayList<NavigationItem>()
    private var mLastClickTime: Long = 0
    private var isSearchAllow: Boolean = false

    enum class TransactionType {
        REPLACE, ADD
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        initialiseListener()
        loadNavigationData()
        setSearchListener()
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        // Creates a vertical Layout Manager
        val navigationItem = NavigationItem()
        navigationItem.itemName = getString(R.string.home)
        navigationItem.itemImage = R.drawable.ic_home
        itemList.add(navigationItem)

        val navigationItem2 = NavigationItem()
        navigationItem2.itemName = getString(R.string.attendance)
        navigationItem2.itemImage = R.drawable.ic_calendar_check_o
        itemList.add(navigationItem2)

        val navigationItem3 = NavigationItem()
        navigationItem3.itemName = getString(R.string.timesheet)
        navigationItem3.itemImage = R.drawable.ic_calendar_o
        itemList.add(navigationItem3)

        val navigationItem4 = NavigationItem()
        navigationItem4.itemName = getString(R.string.settings)
        navigationItem4.itemImage = R.drawable.ic_gears
        itemList.add(navigationItem4)

        val navigationItem5 = NavigationItem()
        navigationItem5.itemName = getString(R.string.logout)
        navigationItem5.itemImage = R.drawable.ic_power_off
        itemList.add(navigationItem5)


        recyclerNavigation.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        navigationAdapter = NavigationAdapter(itemList, this,this)
        recyclerNavigation.adapter = navigationAdapter
    }

    fun lockNavigationMenu(lockMenu: Boolean){
        slidingRootNav?.closeMenu()
        slidingRootNav?.isMenuLocked = lockMenu
    }

    @SuppressLint("SetTextI18n")
    private fun loadNavigationData(){

        slidingRootNav = SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withMenuLayout(R.layout.menu_left_drawer)
            .withMenuLocked(false)
            .inject()

        /*Glide
            .with(this)
            .load(R.drawable.profilepic)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationImageView)*/

        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            txtVersion.text = "Version $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);
        val item = menu!!.findItem(R.id.action_search)
        search_view.setMenuItem(item)
        item.isVisible = isSearchAllow
        return true
    }

    fun setSearchVisibility(isSearchAllow: Boolean){
        this.isSearchAllow = isSearchAllow
        invalidateOptionsMenu()
    }

    /**
     * Listen to search events
     */
     fun setSearchListener() {

        search_view.setVoiceSearch(false); //or false

        /**
         * Detect on text changed for search view
         */
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                MyApplication.getInstance().onQueryTextChange(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                MyApplication.getInstance().onQueryTextChange(newText)
                return true
            }
        })
    }


    private fun initialiseListener() {
     /*  imgToolbarRightIcon.setOnClickListener{

           if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
               mLastClickTime = SystemClock.elapsedRealtime()
               val c = Calendar.getInstance()
               val year = c.get(Calendar.YEAR)
               val month = c.get(Calendar.MONTH)
               val day = c.get(Calendar.DAY_OF_MONTH)
               val datePickerDialog = DatePickerDialog(
                   this,
                   DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                       MyApplication.getInstance().setCalenderDetails(mYear, mMonth, mDay)
                   }, year, month, day
               )
               datePickerDialog.show()
           }
       }*/
    }

    /**
     * Set Toolbar linear layout view dynamically
     */
    fun setToolbarIconLayout(view: View){
        lnrViewToolbar.addView(view)
    }

    /**
     * Remove Toolbar linear layout view
     */
    fun removeToolbarIconLayout(){
        lnrViewToolbar.removeAllViews()
    }

    /**
     * Maintain calendar icon visibility
     */
    fun setToolbarIconVisibility(allow: Boolean){
        /*if (allow)
            imgToolbarRightIcon.visibility = View.VISIBLE
        else imgToolbarRightIcon.visibility = View.GONE*/
    }

    /**
     * Set toolbar visibility
     */
    fun showToolbar(allow: Boolean) {
        if (allow)
            toolbarFrame.visibility = View.VISIBLE
        else
            toolbarFrame.visibility = View.GONE
    }

    /**
     * Set toolbar title
     */
    fun setTitle(title: String){
        toolbarTitle.text = title
    }

    /**
     * Intercept on back click
     */
    override fun onBackPressed() {
        if (getCurrentFragment() is HomeFragment)
            finish()
        else if(search_view.isSearchOpen)
            search_view.closeSearch()
        else
            super.onBackPressed()
    }

    /**
     * Get back stack fragment count
     */
    private fun getFragmentCount(): Int {
        return supportFragmentManager.backStackEntryCount
    }

    /**
     * Get the current displaying fragment
     */
    fun getCurrentFragment(): androidx.fragment.app.Fragment {
        return supportFragmentManager.findFragmentById(R.id.mainContainer)!!
    }
    /**
     * Display a snack message
     */
    fun showSnackAlert(message: String) {
        try {
            Snackbar.make(mainContainer, message, Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Display permission denied alert
     */
    fun showPermissionSnackAlert() {
        val snackBar = Snackbar.make(mainContainer, "Access denied!!", Snackbar.LENGTH_SHORT)
                .setAction("t") {
                    val i = Intent()
                    i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    i.addCategory(Intent.CATEGORY_DEFAULT)
                    i.data = Uri.parse("package:$packageName")
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(i)
                }
        snackBar.show()
        val snackView = snackBar.view
        val action = snackView.findViewById(R.id.snackbar_action) as Button
        action.text = ""
    }

    /**
     * Replace current fragment with new one
     * addToBackStack - true/false keep it in back stack or not
     */
    fun replaceFragment(fragment: androidx.fragment.app.Fragment, addToBackStack: Boolean) {
        inflateFragment(fragment, addToBackStack, TransactionType.REPLACE)
    }

    /**
     * Replace current fragment with new one along with Shared Element Transaction
     * addToBackStack - true/false keep it in back stack or not
     */
    fun replaceFragment(fragment: androidx.fragment.app.Fragment, addToBackStack: Boolean, view: View) {
        inflateFragment(fragment, addToBackStack, TransactionType.REPLACE, view)
    }

    /**
     * Set your fragment layout in the container
     */
    private fun inflateFragment(fragment: androidx.fragment.app.Fragment, addToBackStack: Boolean, transactionType: TransactionType) {
        val transaction = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.simpleName
        if (addToBackStack)
            transaction.addToBackStack(tag)
        when (transactionType) {
            TransactionType.REPLACE -> {
                transaction.replace(R.id.mainContainer, fragment, tag)
            }
        }
        transaction.commitAllowingStateLoss()
    }

    /**
     * Set your fragment layout in the container - this method used for transition element animation
     * Could also be used if any other animation needs to be implemented. Apply check based on animation enum type
     */
    private fun inflateFragment(fragment: androidx.fragment.app.Fragment, addToBackStack: Boolean, transactionType: TransactionType, view: View?) {
        val transaction = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.simpleName
        if (view != null)
            transaction.addSharedElement(view, ViewCompat.getTransitionName(view)!!)
        if (addToBackStack)
            transaction.addToBackStack(tag)
        when (transactionType) {
            TransactionType.REPLACE -> {
                transaction.replace(R.id.mainContainer, fragment, tag)
            }
        }
        transaction.commitAllowingStateLoss()
    }

    /**
     * Pop current displaying fragment
     */
    fun popCurrentFragment() {
        try {
            supportFragmentManager.popBackStack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Pop all fragments from stack
     */
    fun popAllFragments() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            try {
                fm.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Remove fragment with tag in back stack
     */
    fun popFragmentByTag(tag: String) {
        try {
            supportFragmentManager.popBackStack(tag, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Control navigation drawer visibility
     */
    fun showNavigationDrawer(allow: Boolean) {
        slidingRootNav?.isMenuLocked = allow
    }

    /**
     * Set progress bar display
     */
    fun showProgressBar(isAllow: Boolean) {
        if (isAllow)
            progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
    }

    override fun onItemClick(menuItem: String) {
        MyApplication.getInstance().navigationItemTapped(menuItem)
    }
}






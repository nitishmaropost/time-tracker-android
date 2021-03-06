package com.maropost.management.time.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maropost.management.commons.fragments.MPBaseFragment
import com.maropost.management.R
import com.maropost.management.time.pojomodels.RowShifts
import com.maropost.management.time.pojomodels.Shifts
import com.maropost.management.time.view.adapters.ShiftsAdapter
import com.maropost.management.time.view.adapters.ShiftsAdapterCallbacks
import kotlinx.android.synthetic.main.shifts_fragment.*
import java.util.ArrayList

class ShiftsFragment : MPBaseFragment(), ShiftsAdapterCallbacks {

    private var mView: View? = null
    private var shiftsAdapter: ShiftsAdapter? = null
    private val shiftsList = ArrayList<Shifts>()
    private var rowShifts: RowShifts ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mView == null)
            mView = inflater.inflate(R.layout.shifts_fragment, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolbar(true)
        lockNavigationDrawer(true)
        removeToolbarIconLayout()
        setTitle(getString(R.string.shifts))
        initializeRecyclerView()
        updateShiftData()
        setSearchVisibility(false)
    }

    /**
     * Check if shift data is available, then update view
     */
    @SuppressLint("SetTextI18n")
    private fun updateShiftData() {
        if(rowShifts != null && shiftsList.isEmpty()){
            shiftsList.addAll(rowShifts!!.shifts)
            txtName.text = rowShifts?.user_data?.full_name
            txtWorkTime.text = getString(R.string.work_time) + " " + rowShifts?.work_time?.div(3600) + " " + getString(R.string.hrs)
            txtPremisisTime.text = getString(R.string.premisis_time) + " " + rowShifts?.on_premises_time?.div(3600) + " " + getString(R.string.hrs)
            txtPresents.text = getString(R.string.presents) + " " + rowShifts?.presents
            txtAbsents.text = getString(R.string.absents) + " " + rowShifts?.absents
        }
    }

    /*
  * Initialize recycler view and set adapter
  */
    private fun initializeRecyclerView() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            activity!!,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        shiftsAdapter = ShiftsAdapter(shiftsList, activity!!,this)
        shiftsRecyclerView.layoutManager = layoutManager
        //shiftsRecyclerView.adapter?.setHasStableIds(true)
        shiftsRecyclerView.adapter = shiftsAdapter
    }

    /**
     * Set RowShift model to access data
     */
    fun setShiftsData(rowShifts: RowShifts) {
        this.rowShifts = rowShifts
    }

    override fun onItemClick(shifts: Shifts) {
    val timeLogsFragment = TimeLogsFragment()
        timeLogsFragment.setShifts(shifts)
        replaceFragment(timeLogsFragment,true)
    }

}

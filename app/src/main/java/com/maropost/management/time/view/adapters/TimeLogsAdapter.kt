package com.maropost.management.time.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.maropost.management.R
import com.maropost.management.time.pojomodels.TimeLogs
import com.maropost.management.time.pojomodels.Rows
import com.maropost.management.commons.utils.Utility
import kotlinx.android.synthetic.main.item_attendance_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class TimeLogsAdapter(private var arrayList: ArrayList<Rows>, val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<TimeLogsViewHolder>() {

    private var formatter :  SimpleDateFormat ?= null
    private var attendanceDetails: TimeLogs ? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLogsViewHolder {
        return TimeLogsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_attendance_detail, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: TimeLogsViewHolder, position: Int) {

        if(formatter == null)
            formatter = SimpleDateFormat("dd/MM/yyyy")
        val stringTokenizer = StringTokenizer(Utility.getInstance().getEpochTime(arrayList[position].punch_time_str))

        val strDate = stringTokenizer.nextToken() // Extract only date string
        val strTime = stringTokenizer.nextToken() // Extract only time string
        val strAmOrPm = stringTokenizer.nextToken() // Extract AM/PM

        if(position == 0) {
            holder.txtDateHeader?.visibility = View.VISIBLE
            holder.txtDateHeader?.text = strDate
        }
        else{
            // Convert date string to date object
            val itemDate = formatter!!.parse(strDate)

            // Extract only date string for previous entry
            val strPreviousDate = StringTokenizer(Utility.getInstance()
                .getEpochTime(arrayList[position-1].punch_time_str))
                .nextToken()

            // Convert previous date string to date object
            val previousItemDate = formatter!!.parse(strPreviousDate)

            // Compare the two dates - if the Current Item date < Previous date, then display header else remove it
            if (previousItemDate.before(itemDate)) {
                holder.txtDateHeader?.visibility = View.VISIBLE
                holder.txtDateHeader?.text = strDate
            }
            else if(previousItemDate == itemDate)
                holder.txtDateHeader?.visibility = View.GONE
        }

        holder.txtTimeOfPunch?.text = strTime + " " + strAmOrPm.toUpperCase()

        if(attendanceDetails != null) {
            holder.txtPunchType?.text = attendanceDetails!!.pinTypeTextMap?.get(arrayList[position].pin_type.toString())
            when {
                holder.txtPunchType?.text!!.contains("In") -> holder.txtPunchType?.setTextColor(ContextCompat.getColor(context, R.color.colorGreen_900))
                holder.txtPunchType?.text!!.contains("Out") -> holder.txtPunchType?.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                else -> holder.txtPunchType?.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
            }
        }
    }

    /**
     * Set the model framed from the response obtained from Gson
     */
    fun setModel(attendanceDetails: TimeLogs) {
        this.attendanceDetails = attendanceDetails
    }
}

class TimeLogsViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    var txtTimeOfPunch: TextView? = view.txtTimeOfPunch
    var txtPunchType: TextView? = view.txtPunchType
    var txtDateHeader : TextView? = view.txtDateHeader
}
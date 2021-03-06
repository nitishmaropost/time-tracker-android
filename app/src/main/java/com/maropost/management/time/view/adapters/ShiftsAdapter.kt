package com.maropost.management.time.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.maropost.management.R
import com.maropost.management.commons.application.MyApplication
import com.maropost.management.time.pojomodels.Shifts
import com.maropost.management.commons.utils.Utility
import kotlinx.android.synthetic.main.item_shifts_fragment.view.*
import java.util.*

class ShiftsAdapter(private var arrayList: ArrayList<Shifts>, val context: Context,
                    private val shiftsAdapterCallbacks: ShiftsAdapterCallbacks) : androidx.recyclerview.widget.RecyclerView.Adapter<ShiftsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftsViewHolder {
        return ShiftsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shifts_fragment, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShiftsViewHolder, position: Int) {
        holder.txtDate?.text = Utility.getInstance().getDateWithoutTime(arrayList[position].dated_str)
        when(Utility.getInstance().getDateFromString(arrayList[position].dated_str)){
            0 -> setDayValue(holder.txtDay,context.getString(R.string.unknown))
            1 -> setDayValue(holder.txtDay,context.getString(R.string.sunday))
            2 -> setDayValue(holder.txtDay,context.getString(R.string.monday))
            3 -> setDayValue(holder.txtDay,context.getString(R.string.tuesday))
            4 -> setDayValue(holder.txtDay,context.getString(R.string.wednesday))
            5 -> setDayValue(holder.txtDay,context.getString(R.string.thursday))
            6 -> setDayValue(holder.txtDay,context.getString(R.string.friday))
            7 -> setDayValue(holder.txtDay,context.getString(R.string.saturday))
        }
        when(arrayList[position].status){
            context.getString(R.string.holiday) -> {
                holder.txtWorkTime?.text = context.getString(R.string.Holiday)
                holder.txtPremisisTime?.visibility = View.GONE
                holder.txtWorkTime?.setTextColor(ContextCompat.getColor(context, R.color.blue))
                holder.txtWorkTime?.setTypeface(null, Typeface.BOLD)

            }
            context.getString(R.string.absent) -> {
                holder.txtWorkTime?.text = context.getString(R.string.Absent)
                holder.txtPremisisTime?.visibility = View.GONE
                holder.txtWorkTime?.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                holder.txtWorkTime?.setTypeface(null, Typeface.BOLD)
            }
            context.getString(R.string.present) ->{
                holder.txtWorkTime?.setTypeface(null, Typeface.NORMAL)
                holder.txtWorkTime?.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                holder.txtWorkTime?.text = context.getString(R.string.work_time) + " " + arrayList[position].work_time.div(3600) + " " + context.getString(R.string.hrs)
                holder.txtPremisisTime?.text = context.getString(R.string.premisis_time) + " " + arrayList[position].on_premises_time.div(3600) + " " + context.getString(R.string.hrs)
                holder.txtPremisisTime?.visibility = View.VISIBLE
            }
        }

        holder.lnrItem?.setOnClickListener{
            shiftsAdapterCallbacks.onItemClick(arrayList[position])
        }
    }
}

/**
 * Set day of week from date string
 */
private fun setDayValue(txtView: TextView?,weekDay: String){
    txtView?.text = weekDay
    if(weekDay == MyApplication.getInstance().getString(R.string.unknown))
        txtView?.visibility = View.GONE
    else txtView?.visibility = View.VISIBLE
}

interface ShiftsAdapterCallbacks{
    fun onItemClick(shifts: Shifts)
}

class ShiftsViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    var txtDate: TextView? = view.txtDate
    var txtDay: TextView? = view.txtDay
    var txtWorkTime : TextView? = view.txtWorkTime
    var txtPremisisTime : TextView? = view.txtPremisisTime
    var lnrItem : LinearLayout? = view.lnrItem
}
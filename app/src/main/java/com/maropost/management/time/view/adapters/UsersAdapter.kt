package com.maropost.management.time.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.maropost.management.R
import com.maropost.management.time.pojomodels.RowShifts
import kotlinx.android.synthetic.main.item_users_fragment.view.*
import java.util.*
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.graphics.Color
import android.widget.LinearLayout



class UsersAdapter(private var arrayList: ArrayList<RowShifts>, val context: Context,
                   private val attendanceAdapterCallbacks: UsersAdapterCallbacks) : androidx.recyclerview.widget.RecyclerView.Adapter<UsersViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(LayoutInflater.from(context).inflate(R.layout.item_users_fragment, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {

        // Emp name
        holder.txtEmpName?.text = arrayList[position].user_data?.full_name

        // Emp Code
        holder.txtEmpCode?.text = context.getString(R.string.emp_code_) + " " + arrayList[position].user_data?.emp_code

        // Emp email
        val email = arrayList[position].user_data?.email_id
        val builder = SpannableStringBuilder()
        builder.append(context.getString(R.string.email_)+" ")
        val spannable = SpannableString(email)
        spannable.setSpan(ForegroundColorSpan(Color.BLUE), 0, email!!.length, 0)
        builder.append(spannable)
        holder.txtEmail?.setText(builder, TextView.BufferType.SPANNABLE)

        // Emp position
        if (!TextUtils.isEmpty(arrayList[position].user_data?.position)) {
            holder.txtPosition?.text = arrayList[position].user_data?.position
            holder.txtPosition?.visibility = View.VISIBLE
        } else holder.txtPosition?.visibility = View.GONE

        // Emp department
        if (!TextUtils.isEmpty(arrayList[position].user_data?.department)) {
            holder.txtDepartment?.text = arrayList[position].user_data?.department
            holder.txtDepartment?.visibility = View.VISIBLE
        } else holder.txtDepartment?.visibility = View.GONE

        //Card touch event
        holder.lnrCard?.setOnClickListener {
            attendanceAdapterCallbacks.onItemClick(arrayList[position])
        }
    }
}

interface UsersAdapterCallbacks {
    fun onItemClick(rowShifts: RowShifts)
}

class UsersViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    var txtEmpName: TextView? = view.txtEmpName
    var txtPosition: TextView? = view.txtPosition
    var txtDepartment: TextView? = view.txtDepartment
    var txtEmpCode: TextView? = view.txtEmpCode
    var txtEmail: TextView? = view.txtEmail
    var lnrCard: LinearLayout? = view.lnrCard
}
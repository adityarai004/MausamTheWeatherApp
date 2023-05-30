package com.example.mausam_theweatherapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.NonDisposableHandle.parent
import java.lang.System.load

class HourlyAdapter(val mContext: Context, private val mList: List<HoursItemViewModel>) : RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_temp_recycler_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val HoursItemViewModel = mList[position]
        val imgUrl = "https:" + HoursItemViewModel.icon
        Glide.with(mContext).load(imgUrl).into(holder.iconIV)
        holder.tempTV.text = HoursItemViewModel.tempC.toString()
        holder.timeTV.text = HoursItemViewModel.time

    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val iconIV: ImageView = itemView.findViewById(R.id.weather_icon_iv)
        val tempTV: TextView = itemView.findViewById(R.id.temp_tv)
        val timeTV: TextView = itemView.findViewById(R.id.time_tv)
    }
}
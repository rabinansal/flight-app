package com.travelrights.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.travelrights.R
import com.travelrights.activity.PriceListAdapter
import com.travelrights.model.AirportResponse
import kotlinx.android.synthetic.main.item_search.view.*

class AirportAdapter(val context: Context, val list: List<AirportResponse>, val listener: OnItemClickListener)
    : RecyclerView.Adapter<AirportAdapter.AirportViewHolder>(){

    var datasFiltered: List<AirportResponse> = list
    interface OnItemClickListener {
        fun onItemClick(item: AirportResponse, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return AirportViewHolder(
            view
        )
    }

    override fun getItemCount() = datasFiltered.size

    override fun onBindViewHolder(holder: AirportViewHolder, position: Int) {
        val item = datasFiltered[position]
        holder.bind(datasFiltered[position],listener)
        holder.airportCode.text = item.code
        holder.airportPlace.text = item.name
        if(item.main_airport_name!=null){
            holder.airportName.text = item.main_airport_name+",${item.country_name}"
        }
        else{
            holder.airportName.text = "Any airport,${item.country_name}"
        }

    }

    class AirportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val airportCode = itemView.tvCode as TextView
        val airportPlace = itemView.tvAirportPlace as TextView
        val airportName = itemView.tvAirportName as TextView
        val linr_click = itemView.linr_click
        fun bind(item: AirportResponse,listener:OnItemClickListener) {
            linr_click.setOnClickListener(View.OnClickListener { v -> listener.onItemClick(item, v) })
        }
    }



}
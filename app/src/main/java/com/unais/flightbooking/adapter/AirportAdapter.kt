package com.unais.flightbooking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unais.flightbooking.R
import com.unais.flightbooking.model.Airport
import kotlinx.android.synthetic.main.item_search.view.*
import java.util.*

class AirportAdapter(
    val context: Context,
    val list: List<Airport>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<AirportAdapter.AirportViewHolder>() {


    internal var datasFiltered:List<Airport> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirportViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search, parent, false)
        return AirportViewHolder(view)
    }

    override fun getItemCount() = datasFiltered.size

    override fun onBindViewHolder(holder: AirportViewHolder, position: Int) {
        val item = datasFiltered[position]

        val locale = Locale("",item.countryCode)
        holder.airportCode.text = item.code
        holder.airportPlace.text = item.nameTranslations.it
//        holder.airportName.text = locale.displayCountry
        holder.airportName.text = item.name

        holder.itemView.setOnClickListener {
            listener.onItemClick(datasFiltered[position])
        }
    }

    class AirportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val airportCode = itemView.tvCode as TextView
        val airportPlace = itemView.tvAirportPlace as TextView
        val airportName = itemView.tvAirportName as TextView
    }

    interface OnItemClickListener {
        fun onItemClick(item: Airport)
    }


    internal fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    datasFiltered = list
                } else {
                    val filteredList = java.util.ArrayList<Airport>()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.code.toLowerCase().contains(charString.toLowerCase())  || row.code.contains(
                                charSequence
                            ) || row.name.toLowerCase().contains(charString.toLowerCase()) || row.name.contains(
                                charSequence
                            ) || row.nameTranslations.it.toLowerCase().contains(charString.toLowerCase()) || row.nameTranslations.it.contains(
                                charSequence
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    datasFiltered = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = datasFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                datasFiltered = filterResults.values as java.util.ArrayList<Airport>
                notifyDataSetChanged()
            }
        }
    }
}
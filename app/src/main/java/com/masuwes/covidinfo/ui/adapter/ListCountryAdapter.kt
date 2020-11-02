package com.masuwes.covidinfo.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.masuwes.covidinfo.R
import com.masuwes.covidinfo.data.model.CountriesItem
import com.masuwes.covidinfo.ui.activity.ChartCountryActivity
import com.masuwes.covidinfo.ui.activity.ChartCountryActivity.Companion.BUNDLE_KEY

class ListCountryAdapter : RecyclerView.Adapter<ListCountryViewHolder>(), Filterable {

    private val dataCountry = mutableListOf<CountriesItem>()
    private var dataFiltered = mutableListOf<CountriesItem>()

    fun setData(list: List<CountriesItem>) {
        dataCountry.clear()
        dataCountry.addAll(list)
        dataFiltered.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCountryViewHolder =
        ListCountryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false)
        )

    override fun onBindViewHolder(holder: ListCountryViewHolder, position: Int) {
        val data = dataFiltered[position]
        holder.bind(data, isEven(position))

        holder.itemView.setOnClickListener { view ->
            view.context.startActivity(Intent(
                view.context, ChartCountryActivity::class.java
            ).putExtra(BUNDLE_KEY, data))
        }
    }

    override fun getItemCount(): Int = dataFiltered.size

    private fun isEven(number: Int): Boolean = number % 2 == 0

    override fun getFilter(): Filter =
        object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResult = FilterResults()
                Log.e("constraint", constraint.toString())
                dataFiltered.clear()

                if (constraint.isNullOrEmpty()) {
                    dataFiltered.addAll(dataCountry)
                } else {
                    dataCountry.forEach { data ->
                        val countryName = data.country
                        countryName?.let { country ->
                            if (country.contains(constraint, true)) {
                                dataFiltered.add(data)
                            }
                        }
                    }
                }
                filterResult.values = dataFiltered
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }

}
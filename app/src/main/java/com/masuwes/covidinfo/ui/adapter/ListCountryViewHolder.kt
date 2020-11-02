package com.masuwes.covidinfo.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.masuwes.covidinfo.R
import com.masuwes.covidinfo.data.model.CountriesItem
import com.masuwes.covidinfo.helper.loadImage
import kotlinx.android.synthetic.main.list_country.view.*

class ListCountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(data: CountriesItem, isWhite: Boolean) {
        with(itemView) {
            if (isWhite) lay_globe_header.setBackgroundResource(R.color.white)
            else lay_globe_header.setBackgroundResource(R.color.dkgrey)

            txt_country_name.text = data.country
            txt_total_case.text = data.totalConfirmed.toString()
            txt_total_recovered.text = data.totalRecovered.toString()
            txt_total_deaths.text = data.totalDeaths.toString()

            img_flag_country.loadImage("https://www.countryflags.io/${data.countryCode}/flat/16.png")
        }
    }
}
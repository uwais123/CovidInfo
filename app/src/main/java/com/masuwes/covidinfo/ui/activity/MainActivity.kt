package com.masuwes.covidinfo.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.masuwes.covidinfo.R
import com.masuwes.covidinfo.data.model.CountriesItem
import com.masuwes.covidinfo.data.model.Global
import com.masuwes.covidinfo.data.remote.ApiInterface
import com.masuwes.covidinfo.data.remote.ApiService
import com.masuwes.covidinfo.helper.showToast
import com.masuwes.covidinfo.ui.adapter.ListCountryAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var countryAdapter: ListCountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countryAdapter = ListCountryAdapter()

        with(rv_country) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = countryAdapter
        }

        getCovidData(this)

        swipe_refresh.setOnRefreshListener {
            getCovidData(this)
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                countryAdapter.filter.filter(newText)
                Log.e("TestSearchView", newText.toString())
                return false
            }

        })
    }

    private fun getCovidData(mainActivity: MainActivity) {
        lifecycleScope.launch {
            val retrofit = ApiService.buildService(ApiInterface::class.java)
            val summary = retrofit.getSummary()
            if (summary.isSuccessful) {
                val dataCountry = summary.body()?.countries as List<CountriesItem>

                val dataGlobal = summary.body()?.global as Global

                txt_confirmed_glode.text = dataGlobal.totalConfirmed.toString()
                txt_recovered_globe.text = dataGlobal.totalRecovered.toString()
                txt_deaths_glode.text = dataGlobal.totalDeaths.toString()
                progress_bar.visibility = View.GONE
                swipe_refresh.isRefreshing = false

                countryAdapter.setData(dataCountry)
            } else {
                Log.e("RetrofitFailed", summary.errorBody().toString())
                showToast("${summary.errorBody().toString()}")
            }
        }
    }
}

















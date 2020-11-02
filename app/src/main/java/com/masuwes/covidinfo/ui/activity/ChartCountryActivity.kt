package com.masuwes.covidinfo.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.masuwes.covidinfo.R
import com.masuwes.covidinfo.data.model.CountriesItem
import com.masuwes.covidinfo.data.model.ResponseCountry
import com.masuwes.covidinfo.data.remote.ApiInterface
import com.masuwes.covidinfo.data.remote.ApiService
import com.masuwes.covidinfo.helper.loadImage
import kotlinx.android.synthetic.main.activity_chart_country.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChartCountryActivity : AppCompatActivity() {

    private lateinit var dataCountry: CountriesItem
    private val dayCases = mutableListOf<String>()

    private val dataConfirmed = mutableListOf<BarEntry>()
    private val dataDeath = mutableListOf<BarEntry>()
    private val dataRecovered = mutableListOf<BarEntry>()
    private val dataActive = mutableListOf<BarEntry>()


    companion object {
        const val BUNDLE_KEY = "bundle_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_country)

        dataCountry = intent.getParcelableExtra(BUNDLE_KEY)!!

        txt_new_confirmed_current.text = dataCountry.newConfirmed.toString()
        txt_new_deaths_current.text = dataCountry.newDeaths.toString()
        txt_new_recovered_current.text = dataCountry.newRecovered.toString()
        txt_total_confirmed_current.text = dataCountry.totalConfirmed.toString()
        txt_total_deaths_current.text = dataCountry.totalDeaths.toString()
        txt_total_recovered_current.text = dataCountry.totalRecovered.toString()
        txt_current.text = dataCountry.countryCode
        txt_country_chart.text = dataCountry.country

        img_flag_chart.loadImage("https://www.countryflags.io/${dataCountry.countryCode}/flat/64.png")

        dataCountry.slug?.let { slug ->
            getCountryData(slug)
        }
    }

    private fun getCountryData(countryName: String) {
        // panggil retrofit interface (CovidInterface)
        val retrofit = ApiService.buildService(ApiInterface::class.java)

        // membuat variabel format tanggal dari JSON
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'", Locale.getDefault())
        // membuat variabel format output tanggal yang bisa dimengerti manusia
        val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // buat Android Coroutines
        lifecycleScope.launch {
            val countryData = retrofit.getCountryData(countryName)
            // jika data sukses diambil oleh retrofit
            if (countryData.isSuccessful) {
                // buat variabel berisi data tersebut
                val dataCovid = countryData.body() as List<ResponseCountry>

                // lakukan perulangan item dari dataCovid
                dataCovid.forEachIndexed { index, responseCountry ->
                    val barConfirmed =
                        BarEntry(index.toFloat(), responseCountry.Confirmed?.toFloat() ?: 0f)
                    val barDeath =
                        BarEntry(index.toFloat(), responseCountry.Deaths?.toFloat() ?: 0f)
                    val barRecovered =
                        BarEntry(index.toFloat(), responseCountry.Recovered?.toFloat() ?: 0f)
                    val barActive =
                        BarEntry(index.toFloat(), responseCountry.Active?.toFloat() ?: 0f)

                    // tambahkan data bar di atas ke dalam dataConfirmed dll
                    dataConfirmed.add(barConfirmed)
                    dataDeath.add(barDeath)
                    dataRecovered.add(barRecovered)
                    dataActive.add(barActive)

                    // Jika ada tanggal / Date item
                    responseCountry.Date?.let { itemDate ->
                        // parse tanggal dan ubah ke bentuk yang telah diformat sesuai format output
                        val date = inputDateFormat.parse(itemDate)
                        val formattedDate = outputDateFormat.format(date as Date)
                        // tambahkan tanggal yang telah diformat ke dalam dayCases
                        dayCases.add(formattedDate)
                    }
                }

                chart_view.axisLeft.axisMinimum = 0f
                val labelSumbuX = chart_view.xAxis
                labelSumbuX.run {
                    valueFormatter = IndexAxisValueFormatter(dayCases)
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setCenterAxisLabels(true)
                    isGranularityEnabled = true
                }

                // adanya keterangan warna dan legenda
                val barDataConfirmed = BarDataSet(dataConfirmed, "Confirmed")
                val barDataRecovered = BarDataSet(dataRecovered, "Recovered")
                val barDataDeath = BarDataSet(dataDeath, "Death")
                val barDataActive = BarDataSet(dataActive, "Active")

                barDataConfirmed.setColors(Color.parseColor("#F44336"))
                barDataRecovered.setColors(Color.parseColor("#FFEB3B"))
                barDataDeath.setColors(Color.parseColor("#03DAC5"))
                barDataActive.setColors(Color.parseColor("#2196F3"))

                // membuat variabel data berisi semua barData
                val dataChart =
                    BarData(barDataConfirmed, barDataRecovered, barDataDeath, barDataActive)

                // buat variabel berisi spasi
                val barSpace = 0.02f
                val groupSpace = 0.3f
                val groupCount = 4f

                // modifikasi chartView programmatically
                chart_view.run {
                    // Tambahkan dataChart kedalam ChartView
                    data = dataChart
                    // invalidate untuk mengganti data sebelumnya (jika ada) dengan data yang baru
                    invalidate()
                    setNoDataTextColor(R.color.dkgrey)
                    // ChartView bisa ditap atau di-zoom
                    setTouchEnabled(true)
                    description.isEnabled = false
                    xAxis.axisMinimum = 0f
                    setVisibleXRangeMaximum(
                        0f + barData.getGroupWidth(
                            groupSpace,
                            barSpace
                        ) * groupCount
                    )
                    groupBars(0f, groupSpace, barSpace)
                }
            }
        }
    }
}

















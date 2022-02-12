package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.weatherapplication.adapter.ForecastAdapter
import com.example.weatherapplication.databinding.ActivityForecastBinding
import com.example.weatherapplication.model.Forecast
import com.example.weatherapplication.model.ForecastList
import com.example.weatherapplication.network.Api
import com.example.weatherapplication.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForecastBinding
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<Forecast>
    private var forecastList = ArrayList<ForecastList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Forecast"
        retrofitClient = RetrofitClient.getClient(applicationContext).create(Api::class.java)
        getForecast()
    }

    private fun initAdapter(){
        binding.recyclerView.adapter = ForecastAdapter(forecastList)
    }

    private fun getForecast() {
        apiCall = retrofitClient.getForecast("55437","imperial","df5f5ad7dec319cdbd10e03799917453")
        apiCall(apiCall)
    }

    private fun apiCall(call: Call<Forecast>) {

        call.enqueue(object : Callback<Forecast> {
            override fun onResponse(
                call: Call<Forecast>,
                response: Response<Forecast>
            ) {
                try {
                    if (response.isSuccessful) {
                        forecastList.addAll(response.body()?.list!!)
                        initAdapter()
                    } else {
                        println("Exception in api $response")
                        Toast.makeText(applicationContext, response.errorBody()!!.string(), Toast.LENGTH_LONG).show()
                        val errorJSONObject = JSONObject(response.errorBody()!!.string())
                        Log.d("Exception", errorJSONObject.getString("message"))

                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message,Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Forecast>, t: Throwable) {
                Toast.makeText(applicationContext, t.localizedMessage?:"Response Failure",Toast.LENGTH_LONG).show()
                Log.d("Response Failure", t.localizedMessage!!)
            }

        })
    }
}
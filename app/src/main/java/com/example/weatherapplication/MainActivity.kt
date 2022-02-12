package com.example.weatherapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.model.CurrentConditions
import com.example.weatherapplication.network.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofitClient: Api
    private lateinit var apiCall: Call<CurrentConditions>
    private lateinit var currentConditions: CurrentConditions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        retrofitClient = RetrofitClient.getClient(applicationContext).create(Api::class.java)
        supportActionBar?.title = "Weather Application"
        initListeners()
        getCurrentConditions()
    }

    private fun initListeners(){
        binding.btnForecast.setOnClickListener {
            startActivity(Intent(applicationContext, ForecastActivity::class.java))
        }
    }

    private fun getCurrentConditions() {
        apiCall = retrofitClient.getCurrentConditions("55437","imperial","df5f5ad7dec319cdbd10e03799917453")
        apiCall(apiCall)
    }

    private fun apiCall(call: Call<CurrentConditions>) {

        call.enqueue(object : Callback<CurrentConditions> {
            override fun onResponse(
                call: Call<CurrentConditions>,
                response: Response<CurrentConditions>
            ) {
                try {
                    if (response.isSuccessful) {
                        currentConditions = response.body()!!
                        initViews()
                    } else {
                        println("Exception in api $response")
                        Toast.makeText(applicationContext, response.errorBody()!!.string(),Toast.LENGTH_LONG).show()
                        val errorJSONObject = JSONObject(response.errorBody()!!.string())
                        Log.d("Exception", errorJSONObject.getString("message"))

                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message,Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CurrentConditions>, t: Throwable) {
                Toast.makeText(applicationContext, t.localizedMessage?:"Response Failure",Toast.LENGTH_LONG).show()
                Log.d("Response Failure", t.localizedMessage!!)
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(){
        Glide.with(applicationContext).load("http://openweathermap.org/img/wn/${currentConditions.weather[0].icon}@2x.png").into(binding.ivSun)
        binding.tvTemperature.text = "${(currentConditions.main.temp).toInt()}${resources.getString(R.string.degree)}"
        binding.tvFeelLike.text = "Feel like ${(currentConditions.main.feels_like).toInt()}${resources.getString(R.string.degree)}"
        binding.tvHighTemperature.text = "High ${(currentConditions.main.temp_max).toInt()}${resources.getString(R.string.degree)}"
        binding.tvLowTemperature.text = "Low ${(currentConditions.main.temp_min).toInt()}${resources.getString(R.string.degree)}"
        binding.tvHumidity.text = "Humidity ${currentConditions.main.humidity}%"
        binding.tvPressure.text = "Pressure ${currentConditions.main.pressure}"
        binding.tvLocation.text = currentConditions.name
    }
}
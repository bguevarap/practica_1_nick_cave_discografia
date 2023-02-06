package com.bgp.practica1nickcavediscografia.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.Toast
import com.bgp.practica1nickcavediscografia.R
import com.bgp.practica1nickcavediscografia.databinding.ActivityDetailAlbumBinding
import com.bgp.practica1nickcavediscografia.model.AlbumApi
import com.bgp.practica1nickcavediscografia.model.AlbumDetail
import com.bgp.practica1nickcavediscografia.util.Constants
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAlbumActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailAlbumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle = intent.extras

        val id = bundle?.getString("id", "no hay")

        CoroutineScope(Dispatchers.IO).launch {

            val call = Constants.getRetrofit().create(AlbumApi::class.java).getAlbumDetail(id)

            call.enqueue(object : Callback<AlbumDetail> {
                override fun onResponse(call: Call<AlbumDetail>, response: Response<AlbumDetail>) {
                    binding.apply {

                        tvAlbum.text = response.body()?.album
                        tvDate.text = response.body()?.year
                        tvLabel.text = response.body()?.label
                        tvProd.text = response.body()?.producer
                        tvMus.text = response.body()?.musicians
                        tvlongDes.text = response.body()?.longDesc
                        Glide.with(this@DetailAlbumActivity)
                            .load(response.body()?.cover)
                            .into(ivCover)

                        pbConexion.visibility = View.INVISIBLE
                    }

                }

                override fun onFailure(call: Call<AlbumDetail>, t: Throwable) {
                    Toast.makeText(
                        this@DetailAlbumActivity,
                        getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.pbConexion.visibility = View.INVISIBLE
                }

            })

        }
    }
}
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailAlbumActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityDetailAlbumBinding
    private lateinit var map: GoogleMap
    var latitud2 = "51.491502296721265"
    var longitud2 = "-0.13721421207472245"
    lateinit var latitud3:String
    lateinit var longitud3:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle = intent.extras

        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


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

                        latitud3 = response.body()?.latitud.toString()
                        longitud3 = response.body()?.longitud.toString()


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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        createMarker()
    }

    fun createMarker(){
        //19.464089018712155, -99.14044295836308

        val coordinates = LatLng(latitud2.toDouble(), longitud2.toDouble())
        val marker = MarkerOptions()
            .position(coordinates)
            .title(getString(R.string.TITLE))
            .snippet(getString(R.string.ler))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.rnr))


        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }
}
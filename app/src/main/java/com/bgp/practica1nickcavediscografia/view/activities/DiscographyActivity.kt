package com.bgp.practica1nickcavediscografia.view.activities


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bgp.practica1nickcavediscografia.databinding.ActivityMainBinding
import com.bgp.practica1nickcavediscografia.model.Album
import com.bgp.practica1nickcavediscografia.model.AlbumApi
import com.bgp.practica1nickcavediscografia.util.Constants
import com.bgp.practica1nickcavediscografia.view.adapters.AlbumAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.security.GeneralSecurityException


class DiscographyActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(3000)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser
        userId = user?.uid

        binding.tvUserName.text = user?.email


        binding.logOut.setOnClickListener() {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }


        CoroutineScope(Dispatchers.IO).launch {
            val call =
                Constants.getRetrofit().create(AlbumApi::class.java).getAlbums("/discography")
            call.enqueue(object : retrofit2.Callback<ArrayList<Album>> {
                override fun onResponse(
                    call: Call<ArrayList<Album>>,
                    response: Response<ArrayList<Album>>
                ) {
                    Log.d(Constants.LOGTAG, "respuest server: ${response.toString()}")
                    Log.d(Constants.LOGTAG, "body: ${response.body().toString()}")

                    /*val albtemp: Album

                    for(albtemp in response.body()!! ){
                        Toast.makeText(this@DiscographyActivity,"album: ${albtemp.cover}",Toast.LENGTH_SHORT).show()
                    }*/
                    binding.pbConexion.visibility = View.GONE

                    binding.rvPrincipal.layoutManager = LinearLayoutManager(this@DiscographyActivity)
                    binding.rvPrincipal.adapter = AlbumAdapter(this@DiscographyActivity, response.body()!!)

                }

                override fun onFailure(call: Call<ArrayList<Album>>, t: Throwable) {
                    Toast.makeText(
                        this@DiscographyActivity,
                        "no hemos podido accesar al servidor verifica tu conexxión a internet",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.pbConexion.visibility = View.GONE
                }

            })
        }
    }

    fun selectedAlbum(album: Album) {

        val parametros = Bundle().apply {
            putString("id",album.id)
        }

        val intent = Intent(this, DetailAlbumActivity::class.java).apply {
            putExtras(parametros)
        }
        startActivity(intent)

    }

}
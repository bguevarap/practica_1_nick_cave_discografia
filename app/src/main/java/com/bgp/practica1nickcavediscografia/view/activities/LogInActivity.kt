package com.bgp.practica1nickcavediscografia.view.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bgp.practica1nickcavediscografia.R
import com.bgp.practica1nickcavediscografia.databinding.ActivityLogInBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException
import java.security.GeneralSecurityException

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    //Para Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    //Para las shared preferences
    private var usuarioSp: String? = ""
    private var contraseniaSp: String? = ""

    //Lo que ingresa el usuario
    private var email = ""
    private var contrasenia = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(3000)
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()




        binding.btnLogIn.setOnClickListener{
            if(!validaCampos()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE

            autenticaUsuario(email, contrasenia)

        }

        binding.btnRegister.setOnClickListener{
            if(!validaCampos()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE

            //Registrando al usuario
            firebaseAuth.createUserWithEmailAndPassword(email, contrasenia).addOnCompleteListener { authResult->
                if(authResult.isSuccessful){
                    //Enviar correo para verificación de email
                    var user_fb = firebaseAuth.currentUser
                    user_fb?.sendEmailVerification()?.addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.MAIL_ENV_CONF), Toast.LENGTH_SHORT).show()
                    }?.addOnFailureListener {
                        Toast.makeText(this, getString(R.string.NO_MAIL_ENV_CONF), Toast.LENGTH_SHORT).show()
                    }

                    Toast.makeText(this, getString(R.string.CORRECT_USER), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, DiscographyActivity::class.java)
                    intent.putExtra("psw", contrasenia)
                    startActivity(intent)
                    finish()


                }else{
                    binding.progressBar.visibility = View.GONE
                    manejaErrores(authResult)
                }
            }
        }

        binding.btnMissedPass.setOnClickListener{

            val resetMail = EditText(it.context)

            resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            val passwordResetDialog = AlertDialog.Builder(it.context)
            passwordResetDialog.setTitle(getString(R.string.RES_PASS))
            passwordResetDialog.setMessage(getString(R.string.ING_MAIL))
            passwordResetDialog.setView(resetMail)

            passwordResetDialog.setPositiveButton(getString(R.string.BTN_ENV), DialogInterface.OnClickListener { dialog, which ->
                val mail = resetMail.text.toString()
                if(mail.isNotEmpty()){
                    firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {
                        Toast.makeText(this, getString(R.string.ENV_MAIL), Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, getString(R.string.NO_ENV_MAIL), Toast.LENGTH_SHORT).show() //it tiene la excepción
                    }
                }else{
                    Toast.makeText(this, getString(R.string.NO_MAIL), Toast.LENGTH_SHORT).show() //it tiene la excepción
                }
            })

            passwordResetDialog.setNegativeButton(getString(R.string.CANCEL), DialogInterface.OnClickListener { dialog, which ->

            })

            passwordResetDialog.create().show()

        }

    }


    private fun validaCampos(): Boolean{
        email = binding.tvUser.text.toString().trim() //para que quite espacios en blanco
        contrasenia = binding.tvPassword.text.toString().trim()

        if(email.isEmpty()){
            binding.tvUser.error = getString(R.string.ERROR_INVALID_EMAIL)
            binding.tvUser.requestFocus()
            return false
        }

        if(contrasenia.isEmpty() || contrasenia.length < 6){
            binding.tvPassword.error = getString(R.string.ERROR_WEAK_PASSWORD)
            binding.tvPassword.requestFocus()
            return false
        }

        return true
    }

    private fun manejaErrores(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            errorCode = getString(R.string.NO_NETWORK)
        }

        when(errorCode){
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(this, getString(R.string.ERROR_INVALID_EMAIL), Toast.LENGTH_SHORT).show()
                binding.tvUser.error = getString(R.string.ERROR_INVALID_EMAIL)
                binding.tvUser.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.ERROR_WRONG_PASSWORD), Toast.LENGTH_SHORT).show()
                binding.tvPassword.error = getString(R.string.ERROR_WRONG_PASSWORD)
                binding.tvPassword.requestFocus()
                binding.tvPassword.setText("")

            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                Toast.makeText(this, getString(R.string.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL), Toast.LENGTH_SHORT).show()
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(this, getString(R.string.ERROR_EMAIL_ALREADY_IN_USE), Toast.LENGTH_LONG).show()
                binding.tvUser.error = getString(R.string.ERROR_EMAIL_ALREADY_IN_USE)
                binding.tvUser.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(this, getString(R.string.ERROR_USER_TOKEN_EXPIRED), Toast.LENGTH_LONG).show()
            }
            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(this, getString(R.string.ERROR_USER_NOT_FOUND), Toast.LENGTH_LONG).show()
            }
            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.ERROR_WEAK_PASSWORD), Toast.LENGTH_LONG).show()
                binding.tvPassword.error = getString(R.string.ERROR_WEAK_PASSWORD)
                binding.tvPassword.requestFocus()
            }
            "NO_NETWORK" -> {
                Toast.makeText(this, getString(R.string.NO_NETWORK), Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, getString(R.string.NO_ANY_AUTH), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun autenticaUsuario(usr: String, psw: String){

        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            if(authResult.isSuccessful){

                val intent = Intent(this, DiscographyActivity::class.java)
                //intent.putExtra("psw", psw)
                startActivity(intent)
                finish()

            }else{
                binding.progressBar.visibility = View.GONE
                manejaErrores(authResult)
            }
        }
    }

}
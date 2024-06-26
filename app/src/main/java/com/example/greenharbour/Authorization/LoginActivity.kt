package com.example.greenharbour.Authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.bumptech.glide.Glide
import com.example.greenharbour.MainActivity
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var login: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        Glide.with(this).load(R.raw.leaves_flow).into(binding.leavesFlowImg)

        val text =
            "<font color=#808080>Don't have an account ? </font> <font color=#069563>Sign up</font>"
        binding.SignUpText.setText(Html.fromHtml(text))

        binding.loginUpBtn.setOnClickListener {
            login = binding.Login.editText?.text.toString()
            password = binding.password.editText?.text.toString()
            if (login.equals("") or password.equals("")){
                Toast.makeText(this@LoginActivity, "please fill the required field", Toast.LENGTH_SHORT).show()
            }else{
                Firebase.auth.signInWithEmailAndPassword(login,password).addOnCompleteListener {
                    if (it.isSuccessful){
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.SignUpText.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
            finish()
        }
    }
}
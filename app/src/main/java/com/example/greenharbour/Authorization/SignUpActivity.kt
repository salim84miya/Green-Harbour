package com.example.greenharbour.Authorization


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.greenharbour.MainActivity
import com.example.greenharbour.R
import com.example.greenharbour.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var username: String
    private lateinit var login: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        Glide.with(this).load(R.raw.leaves_flow).into(binding.leavesFlowImg)

        val text =
            "<font color=#808080>Have an account ? </font> <font color=#29400E>Login In</font>"
        binding.LoginText.setText(Html.fromHtml(text))

        //handling the username validation
        binding.username.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s?.toString() ?: ""
                validateUsername(newText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        //handling login validation
        binding.Login.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                validateLogin(text.toString())
            }
        })

        //handling password validation
        binding.password.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val pass = s?.toString() ?: ""
                validatePassword(pass)
            }

            override fun afterTextChanged(pass: Editable?) {}
        })

        binding.signUpBtn.setOnClickListener {
            username = binding.username.editText?.text.toString()
            login = binding.Login.editText?.text.toString()
            password = binding.password.editText?.text.toString()

            if (validateFields()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.LoginText.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }
    }

    private fun validateFields(): Boolean {
        return validateUsername(username) && validateLogin(login) && validatePassword(password)
    }

    private fun validateUsername(s: String): Boolean {
        return when {
            s.isEmpty() -> {
                binding.username.error = "username cannot be empty"
                false
            }
            s.length < 6 -> {
                binding.username.error = "username must be 6 characters long"
                false
            }
            else -> {
                binding.username.error = null
                true
            }
        }
    }

    private fun validateLogin(s: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        return when {
            s.isEmpty() -> {
                binding.Login.error = "login cannot be empty"
                false
            }
            !emailRegex.matches(s) -> {
                binding.Login.error = "Invalid email format"
                false
            }
            else -> {
                binding.Login.error = null
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+-=:;'\"\\|/?,.\\s<])(?=\\S+\$).{8,}\$"
        )
        return if (passwordRegex.matches(password)) {
            binding.password.error = null
            true
        } else {
            binding.password.error =
                "Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
            false
        }
    }
}

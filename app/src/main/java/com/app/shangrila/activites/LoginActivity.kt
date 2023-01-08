package com.app.shangrila.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.shangrila.Tools.Constants
import com.app.shangrila.databinding.ActivityLoginBinding
import com.app.shangrila.helper.ApiConfig
import com.app.shangrila.helper.Session
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var session : Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = Session(this@LoginActivity)
        binding.cvSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        binding.cvLogin.setOnClickListener {
            when {
                binding.edEmail.text.toString().isEmpty() || binding.edPassword.text.toString()
                    .isEmpty() -> {
                    Toast.makeText(this, "Check Credentials Once", Toast.LENGTH_LONG).show()
                }
                else -> {
                    loginWithApi(
                        binding.edEmail.text.toString(),
                        binding.edPassword.text.toString()
                    )
                }
            }
        }
    }

    private fun loginWithApi( email : String,password : String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.EMAIL] = email.trim()
        params[Constants.PASSWORD] = password.trim()
        ApiConfig.RequestToVolley({ result, response ->
            Log.d("LOGIN_API_RES",response);
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constants.SUCCESS)) {
                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constants.DATA)
                        session.setBoolean("is_logged_in", true)
                        session.setData(
                            Constants.ID,
                            jsonArray.getJSONObject(0).getString(Constants.ID)
                        )
                        session.setData(
                            Constants.WALLET,
                            jsonArray.getJSONObject(0).getString(Constants.WALLET)
                        )
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(
                            this@LoginActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        this@LoginActivity,
                        "" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    java.lang.String.valueOf(response),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this@LoginActivity, Constants.LOGIN_URL, params, true,1)
    }
}
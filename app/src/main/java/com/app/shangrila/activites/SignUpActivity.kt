package com.app.shangrila.activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.shangrila.Tools.Constants
import com.app.shangrila.databinding.ActivitySignUpBinding
import com.app.shangrila.helper.ApiConfig
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cvSignUp.setOnClickListener {
            when {
                binding.edEmail.text.toString().isEmpty() -> binding.edEmail.apply { this.error = "enterEmail";this.requestFocus() }
                binding.edPassword.text.toString().isEmpty() -> binding.edPassword.apply { this.error = "enterPassword";this.requestFocus() }
                binding.edNoOfBedRooms.text.toString().isEmpty() -> binding.edNoOfBedRooms.apply { this.error = "empty";this.requestFocus() }
                binding.spinProperty.selectedItem.toString().isEmpty() -> Toast.makeText(this@SignUpActivity,"Enter PropertyType",Toast.LENGTH_LONG).show()
                binding.EvcCode.text.toString().isEmpty() -> binding.EvcCode.apply { this.error = "Enter EvcCode";this.requestFocus() }

                else -> signUpWithApi()
            }
        }
    }

    private fun signUpWithApi() {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.EMAIL] = binding.edEmail.text.toString().trim { it <= ' ' }
        params[Constants.PASSWORD] = binding.edPassword.text.toString().trim { it <= ' ' }
        params[Constants.PROPERTY_TYPE] =
            binding.spinProperty.selectedItem.toString().trim { it <= ' ' }
        params[Constants.BEDROOMS_COUNT] = binding.edNoOfBedRooms.text.toString().trim { it <= ' ' }
        params[Constants.EVC_CODE] = binding.EvcCode.text.toString().trim { it <= ' ' }
        ApiConfig.RequestToVolley({ result, response ->
            Log.d("SIGN_UP_RES", response)
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constants.SUCCESS)) {
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(
                            this@SignUpActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, this@SignUpActivity, Constants.SIGNUP_URL, params, true,1)
    }
}
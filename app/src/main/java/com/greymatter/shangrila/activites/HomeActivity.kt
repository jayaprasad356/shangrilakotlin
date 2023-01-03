package com.greymatter.shangrila.activites

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.greymatter.shangrila.R
import com.greymatter.shangrila.Tools.Constants
import com.greymatter.shangrila.databinding.ActivityHomeBinding
import com.greymatter.shangrila.helper.ApiConfig
import com.greymatter.shangrila.helper.Session
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var session: Session
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var walletRechargeBottomSheetDialog : BottomSheetDialog
    private lateinit var datePicker : DatePickerDialog.OnDateSetListener
    private lateinit var calendar: Calendar

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = Session(this@HomeActivity)


        //DatePicker
        calendar = Calendar.getInstance()
        datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            val format = "yyyy-MM-dd"
            val sdf  = SimpleDateFormat(format)
            binding.TvSubmissionDate.text = sdf.format(calendar.time)

        }
        binding.TvSubmissionDate.setOnClickListener {
            DatePickerDialog(this@HomeActivity,
                datePicker,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        //setting balance to wallet
        binding.TvWalletBalance.text = session.getData(Constants.WALLET)

        //initializing bottomSheets
        bottomSheetDialog = BottomSheetDialog(this)
        walletRechargeBottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.apply {
            this.setContentView(R.layout.calculated_amount_bottomsheet)
        }
        walletRechargeBottomSheetDialog.setContentView(R.layout.evc_code_lyt)

        //LogoutFab
        binding.FabLogout.setOnClickListener {
            session.logoutUser(this@HomeActivity)
        }

        //Recharge wallet btn
        binding.CvRechargeWallet.setOnClickListener {
            walletRechargeBottomSheetDialog.show()
        }

        //Recharge wallet BottomSheet Functionality
        val edEvcCode = walletRechargeBottomSheetDialog.findViewById<EditText>(R.id.evcCode)
        walletRechargeBottomSheetDialog.findViewById<CardView>(R.id.CvRecharge)!!.setOnClickListener {
            when {
                edEvcCode!!.text.toString().isNotEmpty() -> rechargeApi(
                    edEvcCode.text.toString().trim()
                )
                else -> Toast.makeText(this@HomeActivity,"Enter evc Code",Toast.LENGTH_LONG).show()
            }
        }

        //DATE Picker

        //btn calculate Functionality
        binding.CvCalculate.setOnClickListener {
            when {
                binding.TvSubmissionDate.text.toString() == "Select Date" -> Toast.makeText(this@HomeActivity,"Enter Date",Toast.LENGTH_LONG).show()
                binding.edMeterDay.text.isEmpty() -> binding.edMeterDay.apply { this.error = "Empty";this.requestFocus() }
                binding.edMeterNight.text.isEmpty() -> binding.edMeterNight.apply { this.error = "Empty";this.requestFocus() }
                binding.edGasMeter.text.isEmpty() -> binding.edGasMeter.apply { this.error = "Empty";this.requestFocus() }
                else -> calculate()
            }
        }
    }

    //Api to Recharge Wallet
    private fun rechargeApi(evc : String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.EVC_CODE] = evc
        params[Constants.USER_ID] = session.getData(Constants.ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constants.SUCCESS)) {
                        walletRechargeBottomSheetDialog.dismiss()
                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constants.DATA)
                        session.setData(
                            Constants.ID,
                            jsonArray.getJSONObject(0).getString(Constants.ID)
                        )
                        session.setData(
                            Constants.WALLET,
                            jsonArray.getJSONObject(0).getString(Constants.WALLET)
                        )
                        Toast.makeText(
                            this@HomeActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@HomeActivity,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this@HomeActivity, Constants.ADD_RECHARGE_URL, params, true)
    }

    //Api to Calculate
    private fun calculate() {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.USER_ID] = session.getData(Constants.ID)
        params[Constants.EMR_DAY] = binding.edMeterDay.text.toString().trim { it <= ' ' }
        params[Constants.EMR_NIGHT] = binding.edMeterNight.text.toString().trim { it <= ' ' }
        params[Constants.GMR] = binding.edGasMeter.text.toString().trim { it <= ' ' }
        params[Constants.DATE] = binding.TvSubmissionDate.text.toString().trim { it <= ' ' }
        ApiConfig.RequestToVolley({ result, response ->
            Log.d("CALCULATE_RESPONSE", response)
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constants.SUCCESS)) {
                        showBottomSheetDialog(jsonObject.getString(Constants.TOTAL_AMOUNT))
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@HomeActivity,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this@HomeActivity, Constants.CALCULATE_BILL_URL, params, true)

    }

    private fun showBottomSheetDialog(amount : String) {
        bottomSheetDialog.show()
        val tv  = bottomSheetDialog.findViewById<TextView>(R.id.TvTotalAmount)
        tv!!.text = amount
        val cv  = bottomSheetDialog.findViewById<CardView>(R.id.CvPayNow)
        cv!!.setOnClickListener {
            payApi(amount)
        }
    }

    private fun payApi(amount : String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constants.USER_ID] = session.getData(Constants.ID)
        params[Constants.EMR_DAY] = binding.edMeterDay.text.toString().trim { it <= ' ' }
        params[Constants.EMR_NIGHT] = binding.edMeterNight.text.toString().trim { it <= ' ' }
        params[Constants.GMR] = binding.edGasMeter.text.toString().trim { it <= ' ' }
        params[Constants.DATE] = binding.TvSubmissionDate.text.toString().trim { it <= ' ' }
        params[Constants.TOTAL] = amount
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constants.SUCCESS)) {
                        bottomSheetDialog.dismiss()
                        val jsonArray = jsonObject.getJSONArray(Constants.DATA)
                        session.setData(
                            Constants.ID,
                            jsonArray.getJSONObject(0).getString(Constants.ID)
                        )
                        session.setData(
                            Constants.WALLET,
                            jsonArray.getJSONObject(0).getString(Constants.WALLET)
                        )
                        Toast.makeText(
                            this@HomeActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "" + jsonObject.getString(Constants.MESSAGE).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this@HomeActivity,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this@HomeActivity, Constants.PAYBILL_URL, params, true)
    }
}
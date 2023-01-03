package com.greymatter.shangrila.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.greymatter.shangrila.R
import com.greymatter.shangrila.helper.Session

class SplashActivity : AppCompatActivity() {
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        session = Session(this)
        Handler().postDelayed({
            if(session.getBoolean("is_logged_in")) {
                startActivity(Intent(this@SplashActivity,HomeActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                finish()
            }
        },4000)
    }
}
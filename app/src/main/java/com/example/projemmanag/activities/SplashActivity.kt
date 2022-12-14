package com.example.projemmanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projemmanag.R
import com.example.projemmanag.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeFace:Typeface= Typeface.createFromAsset(assets,"carbon bl.ttf")
        tv_app_name.typeface=typeFace
        Handler().postDelayed({
            var cuurentUserID= FirestoreClass().getCurrentUserId()
        if (cuurentUserID.isNotEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            startActivity(Intent(this, IntroActivity::class.java))

        }
        finish()
    },2500)
    }
}
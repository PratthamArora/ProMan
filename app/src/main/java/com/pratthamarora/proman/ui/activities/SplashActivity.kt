package com.pratthamarora.proman.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.pratthamarora.proman.R
import com.pratthamarora.proman.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name.typeface = typeface

        Handler().postDelayed(
            {

                val currentUserID = FirestoreClass().getCurrentUserID()

                if (currentUserID.isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
                finish()
            },
            1200
        )
    }
}
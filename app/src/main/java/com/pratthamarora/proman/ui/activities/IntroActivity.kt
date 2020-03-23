package com.pratthamarora.proman.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pratthamarora.proman.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        tv_app_name_intro.typeface = typeface

        btn_sign_in_intro.setOnClickListener {

            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

        btn_sign_up_intro.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
    }
}
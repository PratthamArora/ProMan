package com.pratthamarora.proman.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pratthamarora.proman.R
import com.pratthamarora.proman.firebase.FirestoreClass
import com.pratthamarora.proman.model.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setupActionBar()

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }
        forgot_pass.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        val emailReset = et_email.text.toString().trim()
        if (emailReset.isNotEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailReset).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Please check your email to reset password",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Please enter valid email",
                Toast.LENGTH_LONG
            )
                .show()
        }


    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser() {
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass().loadUserData(this@SignInActivity)
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    fun signInSuccess(user: User) {

        hideProgressDialog()
        Toast.makeText(
            this@SignInActivity,
            "You have successfully signed in.",
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
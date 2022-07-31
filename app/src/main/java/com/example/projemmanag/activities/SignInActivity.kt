package com.example.projemmanag.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projemmanag.R
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()


    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }

    }
    private fun signInRegisteredUser() {
        val email: String = et_email_sign_in.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_in.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        FirestoreClass().loadUserData(this)

                        Toast.makeText(
                            this@SignInActivity,
                            "You have successfully signed in.",
                            Toast.LENGTH_LONG
                        ).show()

                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
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
    fun signInSuccess(user:User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}
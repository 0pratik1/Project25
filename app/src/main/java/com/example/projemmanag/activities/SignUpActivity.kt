package com.example.projemmanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projemmanag.R
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()

    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }
        btn_sign_up.setOnClickListener {
            resisterUser()
        }
    }
    private fun resisterUser(){
        val name:String=et_name.text.toString().trim{it <=' '}
        val email:String=et_email.text.toString().trim{it <=' '}
        val password:String=et_password.text.toString().trim{it <=' '}
        if (validateForm(name,email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registerEmail = firebaseUser.email!!
                    val user=User(firebaseUser.uid,name,registerEmail)
                    FirestoreClass().registerUser(this,user)

                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()

                }
            }
        }
    }
    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a email password")
                false
            }else->{
                true
            }
        }
    }
    fun userRegisterSucess(){
        Toast.makeText(this, "You have sucessusfully register" , Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()

    }
}
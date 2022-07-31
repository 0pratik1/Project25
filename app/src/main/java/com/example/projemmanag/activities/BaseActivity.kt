package com.example.projemmanag.activities

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.projemmanag.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce:Boolean=false
    private lateinit var mProcessDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text:String){
        mProcessDialog= Dialog(this)
        mProcessDialog.setContentView(R.layout.dialog_progress)
        mProcessDialog.tv_progress_text.text=text
        mProcessDialog.show()
    }
    fun hideProgressDialog(){
        mProcessDialog.dismiss()
    }
    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackToExit(){
        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce=true
        Toast.makeText(this
            ,resources.getString(R.string.please_click_back_again_to_exit)
            ,Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackToExitPressedOnce=false},2000)

    }
    fun showErrorSnackBar(message: String){
        val snackbar=Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)
        val snackbarView=snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackbar.show()
    }

}
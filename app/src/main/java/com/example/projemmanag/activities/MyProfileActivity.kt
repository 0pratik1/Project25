package com.example.projemmanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.User
import com.example.projemmanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.lang.Exception
import java.util.jar.Manifest

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri:Uri?=null
    private var mProfileImageURi:String=""
    private lateinit var mUserDatils:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setUpActionBar()
        FirestoreClass().loadUserData(this)
        iv_user_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    Constants.showImagechooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISIION_CODE
                )
            }
        }
        btn_update.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== Constants.READ_STORAGE_PERMISIION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImagechooser(this)
            }
        }else{
            Toast.makeText(this,"You have denied the permisiion allow it through setttings",Toast.LENGTH_LONG).show()

        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title=resources.getString(R.string.my_profile)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }
    fun setUserDataInUI(user:User){
        mUserDatils=user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)
        et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile!=0L){
            et_mobile.setText(user.mobile.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQ_CODE && data!!.data!=null){
            mSelectedImageFileUri=data.data
        try {
            Glide
                .with(this)
                .load(mSelectedImageFileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_user_image)
        }catch (e:IOException){
            e.printStackTrace()
        }

        }

    }
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri!=null){
            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                Log.e("FireBase url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.e("download url",uri.toString())
                    mProfileImageURi=uri.toString()
                    updateUserProfileData()

                }
            }.removeOnFailureListener{
                exception->
                Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    fun updateUserProfileData(){
        val userHashMap=HashMap<String,Any>()

        if (mProfileImageURi.isNotEmpty() && mProfileImageURi!=mUserDatils.image){
            userHashMap[Constants.IMAGE]=mProfileImageURi

        }
        if (et_name.text.toString() !=mUserDatils.name){
            userHashMap[Constants.NAME]=et_name.text.toString()

        }
        if (et_mobile.text.toString() !=mUserDatils.mobile.toString()){
            userHashMap[Constants.MOBILE]=et_mobile.text.toString().toLong()

        }
        FirestoreClass().updateUserProfileData(this,userHashMap)


    }

}
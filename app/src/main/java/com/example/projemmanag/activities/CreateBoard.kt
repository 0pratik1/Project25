package com.example.projemmanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.Board
import com.example.projemmanag.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateBoard : BaseActivity() {
    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserName:String
    private var mBoardImageURL: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setUpActionBar()
        if (intent.hasExtra(Constants.NAME)){
            mUserName=intent.getStringExtra(Constants.NAME)
        }
        iv_board_image.setOnClickListener {
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
        btn_create.setOnClickListener {
            if (mSelectedImageFileUri!=null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }

    }
    private fun createBoard(){
        val assginedUserArrayList:ArrayList<String> = ArrayList()
        assginedUserArrayList.add(getCurrentUserID())
        var board=Board(
            et_board_name.text.toString(),
            mBoardImageURL,
            mUserName,
            assginedUserArrayList
        )
        FirestoreClass().createBoard(this,board)
    }
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
            Log.e("FireBase board url",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                Log.e("download url",uri.toString())
                mBoardImageURL=uri.toString()
                createBoard()

            }
        }.removeOnFailureListener{
                exception->
            Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
            hideProgressDialog()
    }}
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_create_board_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title=resources.getString(R.string.create_board_title)
        }
        toolbar_create_board_activity.setNavigationOnClickListener{
            onBackPressed()
        }}
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQ_CODE && data!!.data!=null){
                mSelectedImageFileUri=data.data
                try {
                    Glide
                        .with(this)
                        .load(mSelectedImageFileUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(iv_board_image)
                }catch (e: IOException){
                    e.printStackTrace()
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
                if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Constants.showImagechooser(this)
                }
            }else{
                Toast.makeText(this,"You have denied the permisiion allow it through setttings",
                    Toast.LENGTH_LONG).show()

            }
        }

}
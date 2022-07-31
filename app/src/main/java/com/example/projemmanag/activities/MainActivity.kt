package com.example.projemmanag.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.ActionMode
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.adapters.BoardItemsAdapter
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.Board
import com.example.projemmanag.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    companion object{
        const val MY_PROFILE_REQ_CODE:Int=11
        const val CREATE_BOARD_REQ_CODE=12
    }

    private lateinit var mUserName:String
    private lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        nav_view.setNavigationItemSelectedListener (this)
        mSharedPreferences=this.getSharedPreferences(Constants.PROJEMANAG_PREFERANCES,Context.MODE_PRIVATE)

        val tokenupdated=mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATE,false)
        if (tokenupdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this,true)

        }else{
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener (this){instanceIdREsult->
                updateFcmToken(instanceIdREsult.token)
            }
        }

        FirestoreClass().loadUserData(this,true)
        fab_create_boards.setOnClickListener {
            val intent=Intent(this,CreateBoard::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQ_CODE)

        }

    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_nav_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQ_CODE)
            }
            R.id.nav_signOut->{
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent=Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    fun tokenUpdateSucess(){
        hideProgressDialog()
        val editor:SharedPreferences.Editor=mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATE,true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this,true)
    }
    fun updateFcmToken(token:String){
        val userHashMap=HashMap<String,Any>()
        userHashMap[Constants.FCM_TOKEN]
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQ_CODE){
            FirestoreClass().loadUserData(this)
        }else if (resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQ_CODE){
            FirestoreClass().getBoardsList(this)
        }
        else{
            Log.e("cancel","Cancelled")
        }

    }
    fun updateNavUserDetails(user:com.example.projemmanag.models.User,readBoardList:Boolean){
        hideProgressDialog()
        mUserName=user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)
        tv_username.text=user.name

        if (readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }

    }
    fun populateBoardsListToUI(boardList: ArrayList<Board>){
        hideProgressDialog()
        if (boardList.size>0){
            rv_boards_list.visibility=View.VISIBLE
            tv_no_boeards_avil.visibility=View.GONE
            rv_boards_list.layoutManager=LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)
            val adapter=BoardItemsAdapter(this,boardList)
            rv_boards_list.adapter=adapter
            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }

            })

        }else{
            rv_boards_list.visibility=View.GONE
            tv_no_boeards_avil.visibility=View.VISIBLE
        }
    }
}
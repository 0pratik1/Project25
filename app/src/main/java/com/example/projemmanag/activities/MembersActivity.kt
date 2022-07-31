package com.example.projemmanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemmanag.R
import com.example.projemmanag.adapters.MembersListItemAdapter
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.Board
import com.example.projemmanag.models.User
import com.example.projemmanag.utils.Constants
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.dialog_search_member.*
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URL

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    lateinit var mAssignedMemberList:ArrayList<User>
    private var anyChangesMade:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        if (intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAILS)

            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assginedTo)
        }
        setUpActionBar()
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_members_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title=resources.getString(R.string.members)
        }
        toolbar_members_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }
    fun setUpMambersList(list: ArrayList<User>){
        mAssignedMemberList=list
        hideProgressDialog()
        rv_members_list.layoutManager=LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)
        val adapter=MembersListItemAdapter(this,list)
        rv_members_list.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun memberDetails(user: User){
        mBoardDetails.assginedTo.add(user.id)
        FirestoreClass().assignedMemberToBoard(this,mBoardDetails,user)

    }
    private fun dialogSearchMember(){
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener {
            val email=dialog.et_email_search_member.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this,email)
            }else{
                Toast.makeText(this,"Please enter email address",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
    }
    fun memberAssignedSuccess(user: User){
        hideProgressDialog()
        anyChangesMade=true
        mAssignedMemberList.add(user)
        setUpMambersList(mAssignedMemberList)
        sendNotificaionToUserAsyncTask(mBoardDetails.name,user.fcmToken).execute()
    }
    inner class sendNotificaionToUserAsyncTask(val boardName:String,val token:String): AsyncTask<Any,Void,String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(resources.getString(R.string.please_wait))

        }
        override fun doInBackground(vararg params: Any?): String {
            var result:String
            var connection:HttpURLConnection?=null
            try {
                val url=URL(Constants.FCM_BASE_URL)
                connection=url.openConnection() as HttpURLConnection
                connection.doOutput=true
                connection.doInput=true
                connection.instanceFollowRedirects=false
                connection.requestMethod="POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(Constants.FCM_AUTHORIZATION,
                    "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")
                connection.useCaches=false
                val wr=DataOutputStream(connection.outputStream)
                val jsonRequest=JSONObject()
                val dataObj=JSONObject()
                dataObj.put(Constants.FCM_KEY_TITLE,"Assigned to the board $boardName")
                dataObj.put(Constants.FCM_KEY_MESSAGE,"You have been assigned to the board by ${mAssignedMemberList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA,dataObj)
                jsonRequest.put(Constants.FCM_KEY_TO,token)
                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()
                val httpResult:Int=connection.responseCode
                if (httpResult==HttpURLConnection.HTTP_OK){
                    val inputStream=connection.inputStream
                    val reader=BufferedReader(InputStreamReader(inputStream))
                    val sb= StringBuilder()
                    var line:String?
                    try {
                        while (reader.readLine().also { line=it}!=null){
                            sb.append(line+"\n")
                        }
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }
                    result=sb.toString()
                }else{
                    result=connection.responseMessage
                }

            }catch (e:SocketTimeoutException){
                result="Connection timeout"
            }catch (e:Exception){
                result="Error"
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()
        }

    }
}
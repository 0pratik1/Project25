package com.example.projemmanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemmanag.R
import com.example.projemmanag.adapters.TaskListItemAdapter
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.Board
import com.example.projemmanag.models.Card
import com.example.projemmanag.models.Task
import com.example.projemmanag.models.User
import com.example.projemmanag.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId:String
    lateinit var mAssignedMemberDeitailsList:ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsDetails(this,mBoardDocumentId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== MEMBER_REQUEST_CODE ||requestCode== CARD_DETAILS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsDetails(this,mBoardDocumentId)
        }else{
            Log.e("cancel","v")
        }
    }
    fun boardDetails(board: Board){
        mBoardDetails=board
        hideProgressDialog()
        setUpActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetails.assginedTo)

    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_task_list_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title=mBoardDetails.name
        }
        toolbar_task_list_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_member->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent, MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun addUpadteTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this,mBoardDetails.documentId)
    }
    fun createTaskList(taskListName: String){
        val task=Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    fun updateTaskList(position: Int,listName:String,model:Task){
        val task=Task(listName,model.createdBy)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)


    }
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUserList:ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())
        val card=Card(cardName,FirestoreClass().getCurrentUserId(),cardAssignedUserList)
        val cardsList=mBoardDetails.taskList[position].cards
        cardsList.add(card)
        val task=Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList)
        mBoardDetails.taskList[position]=task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)


    }
    fun boardMemberDetailsList(list:ArrayList<User>){
        mAssignedMemberDeitailsList=list
        hideProgressDialog()
        val addTaskList=Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)
        rv_task_list.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)
        val adapter=TaskListItemAdapter(this,mBoardDetails.taskList)
        rv_task_list.adapter=adapter
    }
    companion object{
        const val MEMBER_REQUEST_CODE=13
        const val CARD_DETAILS_REQUEST_CODE=14

    }
    fun cardDetails(taskListPosition: Int,cardPosition: Int){
        val intent=Intent(this,CardsDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POS,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POS,cardPosition)
        intent.putExtra(Constants.BOARD_MEMBER_LIST,mAssignedMemberDeitailsList)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }
    fun updateCardsInTaskList(taskListPosition: Int,card:ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        mBoardDetails.taskList[taskListPosition].cards=card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

}
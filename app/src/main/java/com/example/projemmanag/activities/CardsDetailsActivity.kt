package com.example.projemmanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projemmanag.R
import com.example.projemmanag.adapters.CardMemberListItemAdapter
import com.example.projemmanag.dialog.LableColorClassDialog
import com.example.projemmanag.dialog.MembersListDialog
import com.example.projemmanag.firebase.FirestoreClass
import com.example.projemmanag.models.*
import com.example.projemmanag.utils.Constants
import kotlinx.android.synthetic.main.activity_cards_details.*
import kotlinx.android.synthetic.main.activity_members.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardsDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private var mTaskListPosition=-1
    private var mCardPosition=-1
    private var mSelectedColor=""
    private lateinit var mMembersDetailsList: ArrayList<User>
    private var mSelectedDueDAteMilliSeconds:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards_details)
        getIntentData()
        setUpActionBar()
        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        mSelectedColor=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].lableColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this,"please write card name",Toast.LENGTH_SHORT).show()
            }
        }
        tv_select_label_color.setOnClickListener {
            lableColorListDialog()
        }
        tv_select_members.setOnClickListener {
            memberListDialog()
        }
        setUpSelectedMemberList()
        mSelectedDueDAteMilliSeconds=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate
        if (mSelectedDueDAteMilliSeconds>0){
            val simpleDateFormat=SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selectedDate=simpleDateFormat.format(Date(mSelectedDueDAteMilliSeconds))
            tv_select_due_date.text=selectedDate
        }
        tv_select_due_date.setOnClickListener {
            showDataPicker()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card->{
            alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_card_details_activity)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back)
            actionBar.title=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }
        toolbar_card_details_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }
    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAILS)
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POS)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POS,-1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POS)){
            mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POS,-1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBER_LIST)){
            mMembersDetailsList=intent.getParcelableArrayListExtra(Constants.BOARD_MEMBER_LIST)!!
        }

    }
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun updateCardDetails(){
        val card=Card(et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,mSelectedColor,mSelectedDueDAteMilliSeconds)
        val taskList:ArrayList<Task> =mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition]=card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    private fun deleteCard(){
        val cardsList:ArrayList<Card> =mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)
        val taskList:ArrayList<Task> =mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mTaskListPosition].cards=cardsList
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)

    }
    private fun alertDialogForDeleteCard( title: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()// Dialog will be dismissed

        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
            // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    fun colorsList():ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }
    private fun setColor(){
        tv_select_label_color.text=""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }
    private fun memberListDialog(){
        var cardAssignedMembersList=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if (cardAssignedMembersList.size>0){
            for (i in mMembersDetailsList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailsList[i].id==j){
                        mMembersDetailsList[i].selected=true
                    }
                }
            }
        }else{
            for (i in mMembersDetailsList.indices){
                        mMembersDetailsList[i].selected=false
            }
        }
        val listDialog=object :MembersListDialog(this,mMembersDetailsList,resources.getString(R.string.str_select_member)){
            override fun onItemSelected(user: User, action: String) {
                if (action ==Constants.SELECT){
                    if ( !mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)

                    }
                }else{
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)
                    for (i in mMembersDetailsList.indices){
                        if (mMembersDetailsList[i].id==user.id){
                            mMembersDetailsList[i].selected=false
                        }
                    }
                }
                setUpSelectedMemberList()
            }

        }
        listDialog.show()
    }
    private fun lableColorListDialog(){
        val colorsList:ArrayList<String> =colorsList()
        val listDialog=object :LableColorClassDialog(
            this,colorsList,
            resources.getString(R.string.str_select_label_color),mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor=color
                setColor()
            }


        }
        listDialog.show()
    }
    private fun setUpSelectedMemberList(){
        var cardAssignedMemberList= mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo


        val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()
        for (i in mMembersDetailsList.indices){
            for (j in cardAssignedMemberList){
                if (mMembersDetailsList[i].id==j){
                    val selectedMember=SelectedMembers(mMembersDetailsList[i].id,mMembersDetailsList[i].image)
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if (selectedMembersList.size>0){
            selectedMembersList.add(SelectedMembers("",""))
            tv_select_members.visibility=View.GONE
            rv_selected_members_list.visibility=View.VISIBLE
            rv_selected_members_list.layoutManager=GridLayoutManager(this,6)
            val adapter=CardMemberListItemAdapter(this,selectedMembersList,true)
            rv_selected_members_list.adapter=adapter
            adapter.setOnclickListner(object :CardMemberListItemAdapter.OnClickListner{
                override fun onClick() {
                    memberListDialog()
                }
            })
        }else{
            tv_select_members.visibility=View.VISIBLE
            rv_selected_members_list.visibility=View.GONE
        }

    }
    private fun showDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                /*
                  The listener used to indicate the user has finished selecting a date.
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                tv_select_due_date.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                mSelectedDueDAteMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }
}
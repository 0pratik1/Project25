package com.example.projemmanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemmanag.activities.*
import com.example.projemmanag.models.Board
import com.example.projemmanag.models.User
import com.example.projemmanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore=FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity,userInfo: User){
        mFirestore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo,
            SetOptions.merge()).addOnSuccessListener {
            activity.userRegisterSucess()
        }.addOnFailureListener {
            e->

            Log.e(activity.javaClass.simpleName,"error")
        }
    }
    fun createBoard(activity: CreateBoard,board: Board){
        mFirestore.collection(Constants.BOARDS).document().set(board,
            SetOptions.merge()).addOnSuccessListener {
//            activity.userRegisterSucess()
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"error")
        }
    }
    fun loadUserData(activity: Activity,readBoardsList:Boolean=false){
        mFirestore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener {
            document->
            val loggedInUser=document.toObject(User::class.java)
            when(activity){
                is SignInActivity->{
                    activity.signInSuccess(loggedInUser!!)
                }
                is MainActivity->{
                    activity.updateNavUserDetails(loggedInUser!!,readBoardsList)
                }
                is MyProfileActivity->{
                    activity.setUserDataInUI(loggedInUser!!)
                }
            }

        }.addOnFailureListener {

                e->
            when(activity){
                is SignInActivity->{
                    activity.hideProgressDialog()
                }
                is MainActivity->{
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName,"error")
        }
    }
    fun getBoardsDetails(activity: TaskListActivity,documentId:String){
        mFirestore.collection(Constants.BOARDS)
            .document(documentId).get().addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board=document.toObject(Board::class.java)!!
                board.documentId=document.id
                activity.boardDetails(board)


            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }
    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId()).get().addOnSuccessListener {
                document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> =ArrayList()
                for (i in document.documents){
                    val board=i.toObject(Board::class.java)!!
                    board.documentId=i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating")
            }
    }
    fun getCurrentUserId():String{
        var currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if (currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
            Log.i(activity.javaClass.simpleName,"Profile Data update sucessfully!")
            Toast.makeText(activity,"Profile Data update sucessfully!",Toast.LENGTH_LONG).show()
            when(activity){
                is MainActivity->{
                    activity.tokenUpdateSucess()
                }
                is MyProfileActivity->{
                    activity.profileUpdateSuccess()
                }
            }




        }.addOnFailureListener {
            e->
            when(activity){
            is MainActivity->{
                activity.hideProgressDialog()
            }
            is MyProfileActivity->{
                activity.hideProgressDialog()
            }
        }
            Log.i(activity.javaClass.simpleName,"Eror while updating")
            Toast.makeText(activity,"Error while upadating",Toast.LENGTH_LONG).show()

        }

        }
    fun addUpdateTaskList(activity: Activity,board: Board){
        val taskListHashMap=HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]=board.taskList
        mFirestore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                if (activity is TaskListActivity){
                    activity.addUpadteTaskListSuccess()}
                else if (activity is CardsDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }.addOnFailureListener {
                exception->
                if (activity is TaskListActivity){
                    activity.hideProgressDialog()}
                else if (activity is CardsDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.i(activity.javaClass.simpleName,"Eror while creating boerd")

            }
    }
    fun getAssignedMembersListDetails(activity: Activity,assignedTo:ArrayList<String>){
        mFirestore.collection(Constants.USERS).whereIn(Constants.ID,assignedTo).get().addOnSuccessListener {
            document->
            Log.i(activity.javaClass.simpleName,document.documents.toString())
            val userList:ArrayList<User> =ArrayList()
            for (i in document.documents){
                val user=i.toObject(User::class.java)!!
                userList.add(user)
            }
            if (activity is MembersActivity)
                activity.setUpMambersList(userList)
            else if (activity is TaskListActivity)
                activity.boardMemberDetailsList(userList)


        }.addOnFailureListener {
            if (activity is MembersActivity)
                activity.hideProgressDialog()
            else if (activity is TaskListActivity)
                activity.hideProgressDialog()
            Log.i(activity.javaClass.simpleName,"Eror while creating boerd")

        }
    }
    fun getMemberDetails(activity: MembersActivity,email:String){
        mFirestore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email).get().addOnSuccessListener {
                document->
                if (document.documents.size>0){
                    val user=document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such Member Found")
                }
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName,"Eror while creating boerd")

            }
    }
    fun assignedMemberToBoard(activity: MembersActivity,board: Board,user: User){
        val assignedToHashMap=HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assginedTo
        mFirestore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashMap).addOnSuccessListener {
            activity.memberAssignedSuccess(user)
        }.addOnFailureListener {
            activity.hideProgressDialog()
            Log.i(activity.javaClass.simpleName,"Eror while creating boerd")

        }
    }


}
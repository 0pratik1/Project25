package com.example.projemmanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.projemmanag.activities.MyProfileActivity

object Constants{
    const val USERS:String="users"
    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String="mobile"
    const val PICK_IMAGE_REQ_CODE=2
    const val BOARDS:String="boards"
    const val ASSIGNED_TO:String="assginedTo"

    const val READ_STORAGE_PERMISIION_CODE=1
    const val DOCUMENT_ID:String="documentId"
    const val TASK_LIST:String="taskList"
    const val BOARD_DETAILS:String="board_detail"
    const val ID:String="id"
    const val EMAIL:String="email"
    const val TASK_LIST_ITEM_POS:String="task_list_item_position"
    const val CARD_LIST_ITEM_POS:String="card_list_item_position"
    const val BOARD_MEMBER_LIST:String="board_members_list"
    const val SELECT:String="Select"
    const val UNSELECT:String="UnSelect"
    const val PROJEMANAG_PREFERANCES="ProjemanagPrefers"
    const val FCM_TOKEN_UPDATE="fcmTokenUpdates"
    const val FCM_TOKEN="fcmToken"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAAz__Bl_o:APA91bEd4kqNYan0zayNQelFxX0CMdO4MFAAiUZ_HzS1i1dgV7WEorRIX6T-T9onbdwJy93FGrjD1UE7IOA56Ew2QXO1cD3Jy7_B6T9ONhS4hlK_3SGM-HbiuXThCEsVZn1UidQZCzvD"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun showImagechooser(activity: Activity){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQ_CODE)
    }
    fun getFileExtension(activity: Activity,uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
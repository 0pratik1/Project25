package com.example.projemmanag.models

import android.os.Parcel
import android.os.Parcelable
import com.example.projemmanag.activities.CreateBoard
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeStringList

data class Board (
    val name: String="",
    val image:String="",
    val cretedby:String="",
    val assginedTo: ArrayList<String> = ArrayList(),
    var documentId:String="",
    var taskList: ArrayList<Task> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)= with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(cretedby)
        parcel.writeStringList(assginedTo)
        parcel.writeString(documentId)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
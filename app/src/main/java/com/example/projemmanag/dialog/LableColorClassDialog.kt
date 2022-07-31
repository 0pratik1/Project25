package com.example.projemmanag.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemmanag.R
import com.example.projemmanag.adapters.LabelColorListItemAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LableColorClassDialog (
    context: Context,
    private var list: ArrayList<String>,
    private val title:String="",
    private var mSelectedColor:String=""
):Dialog(context){
    private var adapter:LabelColorListItemAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=LayoutInflater.from(context).inflate(
            R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecylerView(view)

    }
    private fun setUpRecylerView(view:View){
        view.tvTitle.text=title
        view.rvList.layoutManager=LinearLayoutManager(context)
        adapter= LabelColorListItemAdapter(context,list, mSelectedColor)
        view.rvList.adapter=adapter
        adapter!!.onItemClickListener=object :LabelColorListItemAdapter.OnClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }
    protected abstract fun onItemSelected(color:String)
}
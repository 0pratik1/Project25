package com.example.projemmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.models.Board
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.nav_header_main.*

open class BoardItemsAdapter(private val context: Context,private var list: ArrayList<Board>) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyviewHolder(LayoutInflater.from(context).inflate(R.layout.item_board,parent,false))


    }
    private var onClickListener:OnClickListener?=null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if (holder is MyviewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_board_image_main)
            holder.itemView.tv_name_board.text=model.name
            holder.itemView.tv_create_by.text="Created by: "+ model.cretedby
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }
    interface OnClickListener{
        fun onClick(position: Int,model:Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private class MyviewHolder(view: View):RecyclerView.ViewHolder(view)


}
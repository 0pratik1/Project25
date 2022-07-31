package com.example.projemmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.models.SelectedMembers
import com.example.projemmanag.models.User
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMemberListItemAdapter (val context: Context,private val list:ArrayList<SelectedMembers>,
                                      val assignMember:Boolean
)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListner: OnClickListner?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(
            R.layout.item_card_selected_member,parent,false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if (holder is MyViewHolder){
            if (position==list.size-1 && assignMember){
                holder.itemView.iv_add_member.visibility=View.VISIBLE
                holder.itemView.iv_selected_member_image.visibility=View.GONE
            }else{

                holder.itemView.iv_add_member.visibility=View.GONE
                holder.itemView.iv_selected_member_image.visibility=View.VISIBLE
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.iv_selected_member_image)
            }
        }
        holder.itemView.setOnClickListener {
            if (onClickListner!=null){
                onClickListner!!.onClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListner{
        fun onClick()
    }
    fun setOnclickListner(onClickListner: OnClickListner){
        this.onClickListner=onClickListner
    }
}
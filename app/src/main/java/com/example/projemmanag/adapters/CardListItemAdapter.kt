package com.example.projemmanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projemmanag.R
import com.example.projemmanag.activities.TaskListActivity
import com.example.projemmanag.models.Board
import com.example.projemmanag.models.Card
import com.example.projemmanag.models.SelectedMembers
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if (holder is MyViewHolder){
            if (model.lableColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.lableColor))
            }else{
                holder.itemView.view_label_color.visibility=View.GONE

            }
            holder.itemView.tv_card_name.text=model.name
            if ((context as TaskListActivity).mAssignedMemberDeitailsList.size>0){
                val selectedMembersList:ArrayList<SelectedMembers> =ArrayList()
                for (i in context.mAssignedMemberDeitailsList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMemberDeitailsList[i].id==j){
                            val selectedMembers=SelectedMembers(
                                context.mAssignedMemberDeitailsList[i].id,
                                context.mAssignedMemberDeitailsList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if (selectedMembersList.size>0){
                    if (selectedMembersList.size==1 && selectedMembersList[0].id==model.createdBy){
                        holder.itemView.rv_card_selected_membersList.visibility=View.GONE
                    }else{
                        holder.itemView.rv_card_selected_membersList.visibility=View.VISIBLE
                        holder.itemView.rv_card_selected_membersList.layoutManager=GridLayoutManager(context,4)
                        val adapter=CardMemberListItemAdapter(context,selectedMembersList,false)
                        holder.itemView.rv_card_selected_membersList.adapter=adapter
                        adapter.setOnclickListner(object :CardMemberListItemAdapter.OnClickListner{
                            override fun onClick() {
                                if (onClickListener!=null){
                                    onClickListener!!.onClick(position)
                                }
                            }

                        })

                    }
                }else{
                    holder.itemView.rv_card_selected_membersList.visibility=View.GONE
                }
            }
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position)

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
    interface OnClickListener{
        fun onClick(position: Int)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }
}
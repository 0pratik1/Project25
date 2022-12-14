package com.example.projemmanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemmanag.R
import com.example.projemmanag.models.User
import com.example.projemmanag.utils.Constants
import kotlinx.android.synthetic.main.item_member.view.*

open class MembersListItemAdapter(private val context: Context,
                                  private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    private var onClickListner:OnClickListner?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text = model.name
            holder.itemView.tv_member_email.text = model.email
            if (model.selected){
                holder.itemView.iv_selected_member.visibility=View.VISIBLE
            }else{
                holder.itemView.iv_selected_member.visibility=View.GONE
            }
            holder.itemView.setOnClickListener {
                if (onClickListner!=null){
                    if (model.selected){
                        onClickListner!!.onClick(position,model,Constants.UNSELECT)
                    }else{
                        onClickListner!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    interface OnClickListner{
        fun onClick(position: Int,user: User,action: String)
    }
    fun setOnclickListner(onClickListner: OnClickListner){
        this.onClickListner=onClickListner
    }
}
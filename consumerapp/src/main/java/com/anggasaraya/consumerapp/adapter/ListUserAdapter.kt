package com.anggasaraya.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anggasaraya.consumerapp.R
import com.anggasaraya.consumerapp.entity.UserFav
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_row.view.*

class ListUserAdapter(private val list:ArrayList<UserFav>) : RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.view.tv_username.text = list[position].username
        Glide.with(holder.itemView.context)
            .load(list[position].avatarURL)
            .into(holder.view.img_avatar)
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(list[position])
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickCallback {
        fun onItemClicked(data: UserFav?)
    }
}
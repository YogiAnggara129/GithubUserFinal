package com.anggasaraya.consumerapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anggasaraya.consumerapp.CustomOnItemClickListener
import com.anggasaraya.consumerapp.DetailActivity
import com.anggasaraya.consumerapp.FavActivity
import com.anggasaraya.consumerapp.R
import com.anggasaraya.consumerapp.entity.UserFav
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.user_row.view.*

class ListUserFavAdapter(private val activity: Activity) : RecyclerView.Adapter<ListUserFavAdapter.ListViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null
    var list = ArrayList<UserFav>()
        set(list) {
            if (list.size > 0) {
                this.list.clear()
            }
            this.list.addAll(list)

            notifyDataSetChanged()
        }

    fun addItem(userFav: UserFav) {
        this.list.add(userFav)
        notifyItemInserted(this.list.size - 1)
    }

    /*fun updateItem(position: Int, userFav: UserFav) {
        this.list[position] = userFav
        notifyItemChanged(position, userFav)
    }*/

    fun removeItem(position: Int) {
        this.list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = list[position]
        holder.view.tv_username.text = item.username
        Glide.with(holder.itemView.context)
            .load(item.avatarURL)
            .into(holder.view.img_avatar)
        holder.view.setOnClickListener(CustomOnItemClickListener(position, object : CustomOnItemClickListener.OnItemClickCallback {
            override fun onItemClicked(view: View, position: Int) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra(FavActivity.EXTRA_POSITION, position)
                intent.putExtra(FavActivity.EXTRA_USERFAV, item)
                activity.startActivityForResult(intent, FavActivity.REQUEST_UPDATE)
            }
        }))
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickCallback {
        fun onItemClicked(data: UserFav?)
    }
}
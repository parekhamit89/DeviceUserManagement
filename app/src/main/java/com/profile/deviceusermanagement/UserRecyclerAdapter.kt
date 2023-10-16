package com.profile.deviceusermanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class UserRecyclerAdapter() :
    ListAdapter<UserData, UserRecyclerAdapter.UserViewHolder>(UserStatusDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item_view, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var userId = view.findViewById<TextView>(R.id.userId)
        var deviceId = view.findViewById<TextView>(R.id.deviceId)
        var status = view.findViewById<TextView>(R.id.status)
        fun bind(item: UserData) {
            userId.text = item.userId
            deviceId.text = "Device ID: ${item.devieId}"


            var binary = hexToBinary(item.userStatus)
            var splitBinary = binary?.split("")
            var auth = "disable"
            var trained = "Not trained on Device"
            var admin = "Admin"
            if(splitBinary?.get(1)?.toInt()==1){
                    auth = "Authorized"
            }
            if(splitBinary?.get(2)?.toInt()==1){
                trained = "Trained on Device"
            }
            if(splitBinary?.get(3)?.toInt()==1){
                admin = "Operator"
            }
            status.text = "Authorize Status= $auth \nTraining Status = $trained \nAdmin Status = $admin"
        }
    }

    class UserStatusDiffUtil : ItemCallback<UserData>() {
        override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {

            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem.userStatus == oldItem.userStatus
        }

    }

}
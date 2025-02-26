package com.app.talent360.home.fragment.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.talent360.databinding.NotificationCardLayoutBinding
import com.app.talent360.home.fragment.notification.NotificationFragment
import com.app.talent360.model.BroadcastNotification
import com.app.talent360.util.convertTimeStamp

class NotificationAdapter(private val listener: NotificationFragment) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val notifications: MutableList<BroadcastNotification> = mutableListOf()

    inner class NotificationViewHolder(
        private val binding: NotificationCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: BroadcastNotification) {
            with(binding) {
                tvNotificationTitle.text = notification.title
                tvMessage.text = notification.body
                tvTimestamp.text = convertTimeStamp(notification.timestamp.toDate())
                ivDelete.setOnClickListener {
                    listener.deleteNotification(notification = notification)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = NotificationCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    fun setData(newNotificationList: List<BroadcastNotification>) {
        notifications.clear()
        notifications.addAll(newNotificationList)
        notifyDataSetChanged()
    }

}
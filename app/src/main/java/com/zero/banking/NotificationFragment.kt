package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifications = listOf(
            Notification(
                getString(R.string.notif_promo_title),
                getString(R.string.notif_promo_msg),
                "PROMO"
            ),
            Notification(
                getString(R.string.notif_security_title),
                getString(R.string.notif_security_msg),
                "SECURITY"
            ),
            Notification(
                getString(R.string.notif_system_title),
                getString(R.string.notif_system_msg),
                "SYSTEM"
            )
        )

        val rvNotifications = view.findViewById<RecyclerView>(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        rvNotifications.adapter = NotificationAdapter(notifications)
    }
}
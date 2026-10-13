package com.example.banking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val username = arguments?.getString("ARG_USERNAME") ?: activity?.intent?.getStringExtra("EXTRA_USERNAME")
        tvProfileName.text = if (username.isNullOrBlank()) getString(R.string.default_username).uppercase() else username.uppercase()

        view.findViewById<LinearLayout>(R.id.btnLanguage).setOnClickListener {
            showLanguageDialog()
        }

        view.findViewById<LinearLayout>(R.id.btnSupport).setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToSecondary(SupportFragment())
        }

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLanguageDialog() {
        (requireActivity() as? DashboardActivity)?.showLanguageDialog()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_confirm_ok) { _, _ ->
                sessionManager.clearSession()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton(R.string.logout_confirm_cancel, null)
            .show()
    }
}
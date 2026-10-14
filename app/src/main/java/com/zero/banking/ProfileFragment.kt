package com.zero.banking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zero.banking.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionManager: SessionManager
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupUserData()
        setupListeners()
    }

    private fun setupUserData() {
        val user = auth.currentUser ?: return
        
        db.collection("users").document(user.uid)
            .addSnapshotListener { snapshot, e ->
                if (isAdded && snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name") ?: ""
                    binding.tvProfileName.text = name.uppercase()
                }
            }
    }

    private fun setupListeners() {
        binding.btnAccountInfo.setOnClickListener {
            startActivity(Intent(requireContext(), AccountInfoActivity::class.java))
        }

        binding.btnChangePin.setOnClickListener {
            startActivity(Intent(requireContext(), UpdatePinActivity::class.java))
        }

        binding.btnLanguage.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.showLanguageDialog()
        }

        binding.btnSupport.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToSecondary(SupportFragment())
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.logout_confirm_ok) { _, _ ->
                auth.signOut()
                sessionManager.clearSession()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton(R.string.logout_confirm_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.zero.banking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup

class QrFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutScan = view.findViewById<LinearLayout>(R.id.layoutScan)
        val layoutMyQR = view.findViewById<LinearLayout>(R.id.layoutMyQR)
        val tvMyQRName = view.findViewById<TextView>(R.id.tvMyQRName)

        val username = arguments?.getString("ARG_USERNAME") ?: activity?.intent?.getStringExtra("EXTRA_USERNAME")
        val displayName = if (username.isNullOrBlank()) getString(R.string.default_username) else username
        tvMyQRName.text = "$displayName / 2847293847"

        view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroupQR).addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnScan -> {
                        layoutScan.visibility = View.VISIBLE
                        layoutMyQR.visibility = View.GONE
                    }
                    R.id.btnMyQR -> {
                        layoutScan.visibility = View.GONE
                        layoutMyQR.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
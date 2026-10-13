package com.example.banking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class SupportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.cardPhone).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:1500888")
            }
            startActivity(intent)
        }

        view.findViewById<MaterialCardView>(R.id.cardEmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@bankingapp.com")
                putExtra(Intent.EXTRA_SUBJECT, "Customer Support Request")
            }
            startActivity(intent)
        }

        view.findViewById<MaterialCardView>(R.id.cardLiveChat).setOnClickListener {
            Toast.makeText(requireContext(), R.string.support_live_chat_toast, Toast.LENGTH_SHORT).show()
        }

        setupFaq(
            view.findViewById(R.id.faqItem1),
            view.findViewById(R.id.faqAnswer1),
            view.findViewById(R.id.faqIcon1)
        )

        setupFaq(
            view.findViewById(R.id.faqItem2),
            view.findViewById(R.id.faqAnswer2),
            view.findViewById(R.id.faqIcon2)
        )

        setupFaq(
            view.findViewById(R.id.faqItem3),
            view.findViewById(R.id.faqAnswer3),
            view.findViewById(R.id.faqIcon3)
        )

        setupFaq(
            view.findViewById(R.id.faqItem4),
            view.findViewById(R.id.faqAnswer4),
            view.findViewById(R.id.faqIcon4)
        )
    }

    private fun setupFaq(item: View, answer: View, icon: ImageView) {
        item.setOnClickListener {
            if (answer.visibility == View.GONE) {
                answer.visibility = View.VISIBLE
                icon.animate().rotation(270f).setDuration(200).start()
            } else {
                answer.visibility = View.GONE
                icon.animate().rotation(90f).setDuration(200).start()
            }
        }
    }
}
package com.example.tiendaonlinehstore.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.utils.SessionManager

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        val textViewUserEmail: TextView = view.findViewById(R.id.textViewUserEmail)
        val btnChangePassword: Button = view.findViewById(R.id.btnChangePassword)

        textViewUserEmail.text = sessionManager.getUserEmail() ?: "Email no encontrado"

        btnChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), "Esta funcionalidad estará disponible pronto", Toast.LENGTH_SHORT).show()
        }
    }
}

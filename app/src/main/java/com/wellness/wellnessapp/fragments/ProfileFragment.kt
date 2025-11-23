package com.wellness.wellnessapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.wellness.wellnessapp.R
import com.wellness.wellnessapp.utils.AuthManager

class ProfileFragment : Fragment() {

    private var authManager: AuthManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Context) {
            authManager = AuthManager(context)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextEmail: TextInputEditText = view.findViewById(R.id.editTextEmail)
        val currentUser = authManager?.getCurrentUser() ?: ""
        if (currentUser.isNotEmpty()) {
            editTextEmail.setText(currentUser)
            editTextEmail.isEnabled = false
        }

        val buttonChangePassword: Button = view.findViewById(R.id.buttonChangePassword)
        buttonChangePassword.setOnClickListener {
            // TODO: Implement change password functionality
            Toast.makeText(requireContext(), "Change Password clicked", Toast.LENGTH_SHORT).show()
        }

        val buttonDeleteAccount: Button = view.findViewById(R.id.buttonDeleteAccount)
        buttonDeleteAccount.setOnClickListener {
            // TODO: Implement delete account functionality
            Toast.makeText(requireContext(), "Delete Account clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.yetanotherjnovelreader.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.Repository
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginDialog() : DialogFragment() {

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.LoginViewModelFactory(Repository.getInstance(requireContext()))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = it.layoutInflater
            val view = inflater.inflate(R.layout.dialog_login, null)
            val errorTextView = view.findViewById<TextView>(R.id.login_error_text)

            MaterialAlertDialogBuilder(it)
                .setTitle("Login")
                .setView(view)
                .setPositiveButton("Confirm") { _, _ ->
                    viewModel.login { loginSuccessful ->
                        if (loginSuccessful) {
                            dialog?.dismiss()
                        } else {
                            errorTextView?.visibility = View.VISIBLE
                        }
                    }
                }.setNegativeButton("Cancel") { _, _ ->
                    dialog?.cancel()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
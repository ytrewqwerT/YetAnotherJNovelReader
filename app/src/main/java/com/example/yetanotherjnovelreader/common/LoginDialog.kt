package com.example.yetanotherjnovelreader.common

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.yetanotherjnovelreader.R
import com.example.yetanotherjnovelreader.data.Repository
import com.example.yetanotherjnovelreader.databinding.DialogLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginDialog : DialogFragment() {

    private var listener: LoginResultListener? = null

    private lateinit var binding: DialogLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModel.LoginViewModelFactory(Repository.getInstance(requireContext()))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = DataBindingUtil.inflate(
                it.layoutInflater, R.layout.dialog_login, null, false
            )
            binding.viewModel = viewModel

            val view = binding.root
            val errorTextView = view.findViewById<TextView>(R.id.login_error_text)
            val dialog = MaterialAlertDialogBuilder(it)
                .setTitle("Login")
                .setView(view)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel") { _, _ ->
                    dialog?.cancel()
                }.create()

            dialog.setOnShowListener {
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener {
                    viewModel.login { loginSuccessful ->
                        if (loginSuccessful) {
                            dialog.dismiss()
                        } else {
                            errorTextView?.visibility = View.VISIBLE
                        }
                    }
                }
            }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? LoginResultListener
    }
}
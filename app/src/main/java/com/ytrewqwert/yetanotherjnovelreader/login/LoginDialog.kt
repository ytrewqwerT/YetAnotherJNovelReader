package com.ytrewqwert.yetanotherjnovelreader.login

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ytrewqwert.yetanotherjnovelreader.R
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.databinding.DialogLoginBinding

class LoginDialog : DialogFragment() {

    private var listener: LoginResultListener? = null

    private lateinit var binding: DialogLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            Repository.getInstance(requireContext())
        )
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
                            listener?.onLoginResult(true)
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
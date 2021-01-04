package com.ytrewqwert.yetanotherjnovelreader.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ytrewqwert.yetanotherjnovelreader.R

/** A dialog for viewing and modifying the user's settings/preferences. */
class SettingsDialogFragment : DialogFragment(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private lateinit var contentView: View

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            contentView = activity.layoutInflater.inflate(R.layout.fragment_settings, null)
            val dialog = MaterialAlertDialogBuilder(activity)
                .setTitle("Settings")
                .setView(contentView)
                .setPositiveButton("Back", null)
                .create()

            dialog.setOnShowListener {
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener { onPositiveButtonPressed() }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childFragmentManager.commit { replace(R.id.container, MainFragment()) }
        return contentView
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat, pref: Preference
    ): Boolean {
        val classLoader = context?.classLoader ?: return false
        val fragment = childFragmentManager.fragmentFactory.instantiate(
            classLoader, pref.fragment
        )
        fragment.arguments = pref.extras
        fragment.setTargetFragment(caller, 0)

        childFragmentManager.commit {
            replace(R.id.container, fragment)
            addToBackStack(null)
        }
        return true
    }

    private fun onPositiveButtonPressed() {
        if (childFragmentManager.backStackEntryCount > 0)
            childFragmentManager.popBackStack()
        else
            dialog?.dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parent = parentFragment ?: activity
        (parent as? DialogInterface.OnDismissListener)?.onDismiss(dialog)
    }
}
package com.example.ecommerce.dialog

import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

// extiende desde Fragment para poder usarlo desde diferentes contextos de la app
fun Fragment.setupBottomSheetDialog(
    onSendClick : (String) -> Unit
){
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED // ?
    dialog.show()

    val edEmailResetPass = view.findViewById<EditText>(R.id.etEmailResetPassword)
    val btnCancel = view.findViewById<AppCompatButton>(R.id.btnCancelResetPassword)
    val btnSend = view.findViewById<AppCompatButton>(R.id.btnResetPassword)

    btnSend.setOnClickListener {
        val email = edEmailResetPass.text.toString()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
}
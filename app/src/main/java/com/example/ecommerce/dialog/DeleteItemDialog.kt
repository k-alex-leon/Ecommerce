package com.example.ecommerce.dialog

import android.app.AlertDialog
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.ecommerce.R

fun Fragment.setupDeleteItemDialog
            (onDeleteClick : () -> Unit){

    val builder = AlertDialog.Builder(requireContext())
    val view = layoutInflater.inflate(R.layout.delete_product_dialog, null)

    builder.setView(view)
    val dialog = builder.create()
    dialog.setCancelable(true)
    dialog.show()

    val btnDelete = view.findViewById<Button>(R.id.btnDeleteItem)
    btnDelete.setOnClickListener {
        onDeleteClick.invoke()
        dialog.dismiss()
    }

}
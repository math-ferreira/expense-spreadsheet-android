package com.math.spreadsheet.util

import android.content.Context

class ToastUtil {

    companion object {

        fun showToast(context: Context, message: String) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT)
                .show()
        }

    }
}
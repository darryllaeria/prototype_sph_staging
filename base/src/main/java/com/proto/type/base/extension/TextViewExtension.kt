package com.proto.type.base.extension

import android.graphics.Color
import android.widget.TextView
import com.proto.type.base.R

// MARK: - TextView
fun TextView.showError(message: Int) {
    showView()
    setTextColor(Color.RED)
    setText(message)
}

fun TextView.showInfo(message: Int) {
    showView()
    setTextColor(context.getColor(R.color.colorPrimaryDark))
    setText(message)
}
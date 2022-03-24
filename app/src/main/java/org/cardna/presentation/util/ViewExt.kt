package org.cardna.presentation.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

fun AppCompatActivity.replace(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .replace(frameId, fragment, null)
        .commit()
}

fun Context.convertDPtoPX(dp: Int): Int {
    val density: Float = this.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}

@SuppressLint("ResourceType")
fun Context.showCenterDialog(@IdRes layout: Int): Dialog {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(layout)
    dialog.setCancelable(false)
    dialog.getWindow()!!.setGravity(Gravity.CENTER)
    dialog.show()
    return dialog
}
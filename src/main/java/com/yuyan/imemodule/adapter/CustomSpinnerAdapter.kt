package com.yuyan.imemodule.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.yuyan.imemodule.data.theme.ThemeManager

class CustomSpinnerAdapter<T>(context: Context, resource: Int, objects: Array<T>) : ArrayAdapter<T>(context, resource, objects) {
    private val textColor: Int = ThemeManager.activeTheme.keyTextColor
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.setTextColor(textColor)
        return view
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.setTextColor(textColor)
        return view
    }
}



package com.yuyan.imemodule.ui.utils

import android.widget.EditText

inline val EditText.str: String
    get() = editableText.toString()



package com.yuyan.imemodule.view.widget

import android.widget.SeekBar

fun SeekBar.setOnChangeListener(listener: SeekBar.(progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            listener.invoke(seekBar, progress)
        }
    })
}

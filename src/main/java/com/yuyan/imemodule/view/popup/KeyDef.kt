
package com.yuyan.imemodule.view.popup

open class KeyDef(
    val popup: Array<Popup>? = null
) {

    sealed class Popup {
        class Key(val label: String) : Popup()
    }
}
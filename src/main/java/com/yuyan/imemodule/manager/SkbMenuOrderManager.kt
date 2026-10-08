package com.yuyan.imemodule.manager

import com.yuyan.imemodule.data.menuSkbFunsPreset
import com.yuyan.imemodule.database.DataBaseKT
import com.yuyan.imemodule.database.dao.SkbFunDao
import com.yuyan.imemodule.database.entry.SkbFun
import com.yuyan.imemodule.entity.SkbFunItem
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils

/**
 * 键盘菜单顺序管理
 */
object SkbMenuOrderManager {

    private const val BAR_MENU = 1
    private const val SETTING_MENU = 0

    @Volatile
    private var barMenuCache: MutableList<SkbFunItem>? = null

    fun barMenus(): MutableList<SkbFunItem> = barMenuCache ?: loadBarMenus().also { barMenuCache = it }

    private fun loadBarMenus(): MutableList<SkbFunItem> {
        val items = mutableListOf<SkbFunItem>()
        DataBaseKT.instance.skbFunDao().getALlBarMenu().forEach { bean ->
            menuSkbFunsPreset[SkbMenuMode.decode(bean.name)]?.let(items::add)
        }
        return items
    }

    fun invalidate() {
        barMenuCache = null
    }

    fun isInBarMenu(skbMenuMode: SkbMenuMode): Boolean = barMenus().any { it.skbMenuMode == skbMenuMode }

    fun addBarMenu(skbMenuMode: SkbMenuMode): Boolean {
        val item = menuSkbFunsPreset[skbMenuMode] ?: return false
        val menus = barMenus()
        if (menus.any { it.skbMenuMode == skbMenuMode }) return false
        menus.add(item)
        writeBarOrder(menus.map { it.skbMenuMode })
        return true
    }

    fun removeBarMenu(skbMenuMode: SkbMenuMode): Boolean {
        val menus = barMenus()
        if (!menus.removeAll { it.skbMenuMode == skbMenuMode }) return false
        val order = menus.map { it.skbMenuMode }
        write { dao ->
            dao.deleteBarMenu(skbMenuMode.name)
            order.forEachIndexed { index, mode ->
                dao.insert(SkbFun(name = mode.name, isKeep = BAR_MENU, position = index))
            }
        }
        return true
    }

    fun persistBarMenus(menus: List<SkbFunItem>) {
        barMenuCache = menus.toMutableList()
        writeBarOrder(menus.map { it.skbMenuMode })
    }

    fun persistSettingMenus(menus: List<SkbFunItem>) = persistOrder(menus.map { it.skbMenuMode }, SETTING_MENU)

    private fun writeBarOrder(order: List<SkbMenuMode>) {
        write { dao ->
            order.forEachIndexed { index, mode ->
                dao.insert(SkbFun(name = mode.name, isKeep = BAR_MENU, position = index))
            }
        }
    }

    private fun persistOrder(order: List<SkbMenuMode>, isKeep: Int) {
        write { dao ->
            order.forEachIndexed { index, mode ->
                // 用 REPLACE 语义写入：@Update 在行不存在时会静默失败（例如刚添加还没落盘就拖拽）
                dao.insert(SkbFun(name = mode.name, isKeep = isKeep, position = index))
            }
        }
    }

    private fun write(block: (SkbFunDao) -> Unit) {
        ThreadPoolUtils.executeSingleton {
            runCatching { block(DataBaseKT.instance.skbFunDao()) }
                .onFailure { invalidate() }
        }
    }
}

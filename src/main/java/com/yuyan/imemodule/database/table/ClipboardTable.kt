package com.yuyan.imemodule.database.table

/**
 * 剪贴板表
 * @author KongXR
 */
object ClipboardTable {
    const val TABLE_NAME = "user_clipboard_table" //表名
    const val CONTENT_ID = "content_id" //复制内容
    const val COPY_CONTENT = "copy_content" //复制内容
    const val COPY_TIME = "copy_time" //复制时间
    const val IS_KEEP = "is_keep" //是否收藏
    const val COPY_EXTENDS = "copy_extends" //扩展字段
    const val CREATE_TABLE = ("create table if not exists " + TABLE_NAME + " ("
            + CONTENT_ID + " integer primary key autoincrement,"
            + COPY_CONTENT + " text,"
            + IS_KEEP + " integer default 0,"
            + COPY_EXTENDS + " text,"
            + COPY_TIME + " timestamp NOT NULL DEFAULT (datetime('now','localtime')))")
}

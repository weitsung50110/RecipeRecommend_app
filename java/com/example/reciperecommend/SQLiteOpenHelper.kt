package com.example.reciperecommend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
// 基本上，這裡面的東西都不用修改
class MyDBHelper(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    var sCreateTableCommand = ""
    // 建立應用程式需要的表格
    override fun onCreate(db: SQLiteDatabase) {
        if (sCreateTableCommand.isEmpty()) return
        db.execSQL(sCreateTableCommand)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        // TODO Auto-generated method stub
    }
}
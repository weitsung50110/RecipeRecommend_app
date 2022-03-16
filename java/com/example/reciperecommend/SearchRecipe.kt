package com.example.reciperecommend

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_search_recipe.*

class SearchRecipe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_recipe)
        getData()
        imageButton_RecomSearch.setOnClickListener(mListener_Main3)
        SearchRecip_Cancel.setOnClickListener(mListener_Cancel)
        imageButton_HomeSearch.setOnClickListener(mListener_GoHome)
        imageButton_RecordSearch.setOnClickListener(mListener_GoRecord)
        imageButton_HistorySearch.setOnClickListener(mListener_GoHistory)

        SearchRecipe_Search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!=null){
                    Recycler_SearchRecipe.setVisibility(View.GONE)
                    Recycler_SearchKey.setVisibility(View.VISIBLE)
                }
                else if (count == 0){
                    Recycler_SearchRecipe.setVisibility(View.VISIBLE)
                }

                val arrayList_SName = ArrayList<String>()
                val arrayList_SType = ArrayList<String>()
                val arrayList_Table = ArrayList<String>()

                //切割關鍵字
                val arrayList_Input = SearchRecipe_Search.text.toString().split(" ")
//                for (str in arrayList_Input){
//                }

                val DB_FILE = "Food.db"
                val DB_TABLE_Recipe = "RecipeName"

                val MyDB: SQLiteDatabase
                // 建立自訂的 FriendDbHelper 物件
                val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)

                MyDB = friDbHp.writableDatabase

                //食譜
                val r = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Recipe +" WHERE Name like '%" + SearchRecipe_Search.text.toString() + "%'", arrayOf<String>())
                if (r.count === 0) {

                }
                else {
                    r.moveToFirst()
                    arrayList_SName.add(r.getString(2))
                    arrayList_SType.add(r.getString(1))
                    arrayList_Table.add("RecipeSearch")
                    while (r.moveToNext()) {
                        arrayList_SName.add(r.getString(2))
                        arrayList_SType.add(r.getString(1))
                        arrayList_Table.add("RecipeSearch")
                    }
                }

                if (r.count === 0) {
                    arrayList_SName.add("沒有資料")
                    arrayList_SType.add("沒有資料")
                    arrayList_Table.add("沒有資料")
                }
                val layoutManager = LinearLayoutManager(this@SearchRecipe)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                Recycler_SearchKey.layoutManager = layoutManager
                Recycler_SearchKey.adapter = DataAdapter_Search(arrayList_SName,arrayList_SType,arrayList_Table,this@SearchRecipe)
            }
        }) //關鍵字搜尋

    }

    fun getData(){
        val DB_FILE = "Food.db"
        val DB_TABLE_Type = "RecipeType"
        val DB_TABLE_Name = "RecipeSearch"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)

        MyDB = friDbHp.writableDatabase
        val arrayList_Type = ArrayList<String>()
//        val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE, arrayOf<String>())
        val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Type, arrayOf<String>())
        if (c.count === 0) {
            arrayList_Type.add("沒有資料")
        }
        else {
            c.moveToFirst()
            arrayList_Type.add(c.getString(1))
            while (c.moveToNext()) {
                arrayList_Type.add(c.getString(1))
            }
        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        Recycler_SearchRecipe.layoutManager = layoutManager
        Recycler_SearchRecipe.adapter = DataAdapter(DB_TABLE_Name,arrayList_Type,this)

    }
    private val mListener_Main3 = View.OnClickListener {
        val dialog = ProgressDialog.show(this@SearchRecipe,"讀取中", "請等待...", true)
        Thread(Runnable {
            try {
                Thread.sleep(10000)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dialog!!.dismiss()
            }
        }).start()

        val intent = Intent()
        intent.setClass(this, Recommand_0::class.java)
        startActivity(intent)

    }
    private val mListener_GoHome = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@SearchRecipe, Main2Activity::class.java)
        startActivity(intent)
    }
    private val mListener_GoRecord = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@SearchRecipe, Record::class.java)
        startActivity(intent)
    }
    private val mListener_GoHistory = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@SearchRecipe, History::class.java)
        startActivity(intent)
    }
    private val mListener_Cancel = View.OnClickListener {
        SearchRecipe_Search.setText(null)
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
        Recycler_SearchKey.setVisibility(View.GONE)
        Recycler_SearchRecipe.setVisibility(View.VISIBLE)
    }
}
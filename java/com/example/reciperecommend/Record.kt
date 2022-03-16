package com.example.reciperecommend

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_record.*


class Record : AppCompatActivity() {
    var Calories:Float = 0f
    private lateinit var pageList: MutableList<PageView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        //軟鍵盤搜尋
//        Record_SearchInput.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                Toast.makeText(this, "哈哈", Toast.LENGTH_SHORT).show()
//            }
//            false
//        }
        imageButton_RecomRecord.setOnClickListener(mListener_Main3)
        imageButton_HistoryRecord.setOnClickListener(mListener_History)
        imageButton_HomeRecord.setOnClickListener(mListener_Main2)
        imageButton_SearchRecord.setOnClickListener(mListener_Search)
        //關鍵字搜尋
        Record_SearchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!=null){
                    tabs.setVisibility(View.GONE)
                    viewpager.setVisibility(View.GONE)
                    Search_Recycler.setVisibility(View.VISIBLE)
                }
                else if (count == 0){
                    tabs.setVisibility(View.VISIBLE)
                }

                val arrayList_SName = ArrayList<String>()
                val arrayList_SType = ArrayList<String>()
                val arrayList_Table = ArrayList<String>()

                //切割關鍵字
                val arrayList_Input = Record_SearchInput.text.toString().split(" ")
//                for (str in arrayList_Input){
//                }

                val DB_FILE = "Food.db"
                val DB_TABLE_Recipe = "RecipeName"
                val DB_TABLE_Inger = "IngerName"
                val DB_TABLE_Out = "OutName"

                val MyDB: SQLiteDatabase
                // 建立自訂的 FriendDbHelper 物件
                val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)

                MyDB = friDbHp.writableDatabase

                //外食

                val o = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Out +" WHERE Name like '%" + Record_SearchInput.text.toString() + "%'", arrayOf<String>())


                if (o.count === 0) {

                }
                else {
                    o.moveToFirst()
                    arrayList_SName.add(o.getString(2))
                    arrayList_SType.add(o.getString(1))
                    arrayList_Table.add(DB_TABLE_Out)
                    while (o.moveToNext()) {
                        arrayList_SName.add(o.getString(2))
                        arrayList_SType.add(o.getString(1))
                        arrayList_Table.add(DB_TABLE_Out)
                    }
                }

                //食譜
                val r = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Recipe +" WHERE Name like '%" + Record_SearchInput.text.toString() + "%'", arrayOf<String>())
                if (r.count === 0) {

                }
                else {
                    r.moveToFirst()
                    arrayList_SName.add(r.getString(2))
                    arrayList_SType.add(r.getString(1))
                    arrayList_Table.add(DB_TABLE_Recipe)
                    while (r.moveToNext()) {
                        arrayList_SName.add(r.getString(2))
                        arrayList_SType.add(r.getString(1))
                        arrayList_Table.add(DB_TABLE_Recipe)
                    }
                }

                //食材
                val i = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Inger +" WHERE Name like '%" + Record_SearchInput.text.toString() + "%'", arrayOf<String>())
                if (i.count === 0) {

                }
                else {
                    i.moveToFirst()
                    arrayList_SName.add(i.getString(2))
                    arrayList_SType.add(i.getString(1))
                    arrayList_Table.add(DB_TABLE_Inger)
                    while (i.moveToNext()) {
                        arrayList_SName.add(i.getString(2))
                        arrayList_SType.add(i.getString(1))
                        arrayList_Table.add(DB_TABLE_Inger)
                    }
                }

                if (i.count === 0 && r.count === 0 && o.count === 0) {
                    arrayList_SName.add("沒有資料")
                    arrayList_SType.add("沒有資料")
                    arrayList_Table.add("沒有資料")
                }
                val layoutManager = LinearLayoutManager(this@Record)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                Search_Recycler.layoutManager = layoutManager
                Search_Recycler.adapter = DataAdapter_Search(arrayList_SName,arrayList_SType,arrayList_Table,this@Record)
            }
            }) //關鍵字搜尋

        //關鍵字搜尋旁邊的取消鍵
        Search_Cancel.setOnClickListener(mListener_Cancel)

        initData()
        initView()

        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewpager))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })



    }
    private val mListener_Main3 = View.OnClickListener {
        val dialog = ProgressDialog.show(this@Record,"讀取中", "請等待...", true)
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
    private val mListener_Main2 = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Record, Main2Activity::class.java)
        startActivity(intent)
    }
    private val mListener_History = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Record, History::class.java)
        startActivity(intent)
    }
    private val mListener_Search = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Record, SearchRecipe::class.java)
        startActivity(intent)
    }
    private val mListener_Cancel = View.OnClickListener {
        Record_SearchInput.setText(null) //editText內容清空
        tabs.setVisibility(View.VISIBLE)
        viewpager.setVisibility(View.VISIBLE)
        Search_Recycler.setVisibility(View.GONE)
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
    private fun initView() {
        viewpager.adapter = SamplePagerAdapter(pageList)
    }

    private fun initData() {
        pageList = ArrayList()
        pageList.add(RecordPageOne(this@Record)) //食譜
        pageList.add(RecordPageTwo(this@Record)) //食材
        pageList.add(RecordPageThree(this@Record)) //外食
    }

}



class RecordPageOne(context: Context) : PageView(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.record_page, null)

        val DB_FILE = "Food.db"
        val DB_TABLE_Type = "RecipeType"
        val DB_TABLE_Name = "RecipeName"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(context.applicationContext, DB_FILE, null, 1)

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


        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val dataList_out = view.findViewById<RecyclerView>(R.id.Recycle_Food)
        dataList_out.layoutManager = layoutManager
        dataList_out.adapter = DataAdapter(DB_TABLE_Name,arrayList_Type,this.context)
        addView(view)
    }

    override fun refreshView() {

    }
}

class RecordPageTwo(context: Context) : PageView(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.record_page, null)

        val DB_FILE = "Food.db"
        val DB_TABLE_Type = "IngerType"
        val DB_TABLE_Name = "IngerName"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(context.applicationContext, DB_FILE, null, 1)

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


        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val dataList_out = view.findViewById<RecyclerView>(R.id.Recycle_Food)
        dataList_out.layoutManager = layoutManager
        dataList_out.adapter = DataAdapter(DB_TABLE_Name,arrayList_Type,this.context)
        addView(view)
    }

    override fun refreshView() {

    }
}
class RecordPageThree(context: Context) : PageView(context) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.record_page, null)

        val DB_FILE = "Food.db"
        val DB_TABLE_Type = "OutType"
        val DB_TABLE_Name = "OutName"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(context.applicationContext, DB_FILE, null, 1)

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


        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val dataList_out = view.findViewById<RecyclerView>(R.id.Recycle_Food)
        dataList_out.layoutManager = layoutManager
        dataList_out.adapter = DataAdapter(DB_TABLE_Name,arrayList_Type,this.context)
        addView(view)
    }

    override fun refreshView() {

    }
}

class DataAdapter(private var DataType: String, private val mData: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_item, null, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dataView_Name.text = mData[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(context,Record_Detail::class.java)
            intent.putExtra("getType", mData[position])
            intent.putExtra("DataType", DataType)
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.RecordItem_Name)

}


class DataAdapter_Search(private val oName: List<String>,private val oType: List<String>,private val oTable: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_Search>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Search {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_item, null, false)
        return ViewHolder_Search(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_Search, position: Int) {
        holder.dataView_Name.text = oName[position]
        holder.dataView_Type.text = oType[position]
        holder.itemView.setOnClickListener {
            if (oTable[position] == "RecipeName")
            {
                val intent = Intent(context,Record_Recipe::class.java)
                intent.putExtra("getRecipe", oName[position])
                context.startActivity(intent)
            }
            else if (oTable[position] == "OutName")
            {
                val intent = Intent(context,Record_Out::class.java)
                intent.putExtra("getOut", oName[position])
                context.startActivity(intent)
            }
            else if (oTable[position] == "IngerName")
            {
                val intent = Intent(context,Record_Inger::class.java)
                intent.putExtra("getInger", oName[position])
                context.startActivity(intent)
            }
            else if (oTable[position] == "RecipeSearch")
            {
                val intent = Intent(context,ShowRecipe::class.java)
                intent.putExtra("getSearch", oName[position])
                context.startActivity(intent)
            }
            true
        }
        holder.itemView.setOnLongClickListener {
//            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }
    override fun getItemCount(): Int {
        return oName.size
    }
}
class ViewHolder_Search(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.RecordItem_Name)
    val dataView_Type: TextView = v.findViewById(R.id.RecordItem_Name_Type)
}
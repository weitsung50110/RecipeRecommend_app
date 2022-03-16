package com.example.reciperecommend

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_record__detail.*

class Record_Detail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record__detail)

        val Type = intent.getStringExtra("getType")
        val DataType = intent.getStringExtra("DataType")
        getData(Type,DataType)

        Record_Detail_Cancel.setOnClickListener(mListener_RecordDetail)
    }

    private val mListener_RecordDetail = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@Record_Detail, Record::class.java)
        startActivity(intent);
    }
    fun getData(Type:String,DB_TABLE_Name:String){
        if (DB_TABLE_Name == "RecipeSearch")
        {
            val DB_FILE = "Food.db"
            val MyDB: SQLiteDatabase
            // 建立自訂的 FriendDbHelper 物件
            val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)

            MyDB = friDbHp.writableDatabase
            val RecordDetail_Type = ArrayList<String>()
            val RecordDetail_Name = ArrayList<String>()

            val c = MyDB.rawQuery("SELECT * FROM  "+"RecipeName"+" WHERE Type = '"+Type+"'", arrayOf<String>())

            if (c.count === 0) {
                RecordDetail_Name.add("沒有資料")
            }
            else {
                c.moveToFirst()
                RecordDetail_Name.add(c.getString(2))
                RecordDetail_Type.add(c.getString(1))
                while (c.moveToNext()) {
                    RecordDetail_Name.add(c.getString(2))
                    RecordDetail_Type.add(c.getString(1))
                }
            }
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            Recycler_Detail.layoutManager = layoutManager
            Recycler_Detail.adapter = DataAdapter_Detail(DB_TABLE_Name,RecordDetail_Name,RecordDetail_Type,this)
        }
        else
        {
            val DB_FILE = "Food.db"
            val MyDB: SQLiteDatabase
            // 建立自訂的 FriendDbHelper 物件
            val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)

            MyDB = friDbHp.writableDatabase
            val RecordDetail_Type = ArrayList<String>()
            val RecordDetail_Name = ArrayList<String>()

            val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Name+" WHERE Type = '"+Type+"'", arrayOf<String>())

            if (c.count === 0) {
                RecordDetail_Name.add("沒有資料")
            }
            else {
                c.moveToFirst()
                RecordDetail_Name.add(c.getString(2))
                RecordDetail_Type.add(c.getString(1))
                while (c.moveToNext()) {
                    RecordDetail_Name.add(c.getString(2))
                    RecordDetail_Type.add(c.getString(1))
                }
            }
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            Recycler_Detail.layoutManager = layoutManager
            Recycler_Detail.adapter = DataAdapter_Detail(DB_TABLE_Name,RecordDetail_Name,RecordDetail_Type,this)
        }


    }


}

class DataAdapter_Detail(private var Detail_DataType: String, private val mData: List<String>,private val mType: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_Detail>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Detail {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_item, null, false)
        return ViewHolder_Detail(v)
    }

    override fun onBindViewHolder(holder: ViewHolder_Detail, position: Int) {
        holder.dataView_Name.text = mData[position]
        holder.dataView_Type.text = mType[position]
        holder.itemView.setOnClickListener {
            if (Detail_DataType == "RecipeName")
            {
                Toast.makeText(it.context, "已新增至資料庫，若要刪除請點選取消", Toast.LENGTH_SHORT).show()
                val intent = Intent(context,Record_Recipe::class.java)
                intent.putExtra("getRecipe", mData[position])
                context.startActivity(intent)
            }
            else if (Detail_DataType == "IngerName")
            {
                Toast.makeText(it.context, "已新增至資料庫，若要刪除請點選取消", Toast.LENGTH_SHORT).show()
                val intent = Intent(context,Record_Inger::class.java)
                intent.putExtra("getInger", mData[position])
                context.startActivity(intent)
            }
            else if (Detail_DataType == "OutName")
            {
                Toast.makeText(it.context, "已新增至資料庫，若要刪除請點選取消", Toast.LENGTH_SHORT).show()
                val intent = Intent(context,Record_Out::class.java)
                intent.putExtra("getOut", mData[position])
                context.startActivity(intent)
            }
            else if (Detail_DataType == "RecipeSearch")
            {
                val intent = Intent(context,ShowRecipe::class.java)
                intent.putExtra("getSearch", mData[position])
                context.startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener {
//            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class ViewHolder_Detail(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.RecordItem_Name)
    val dataView_Type: TextView = v.findViewById(R.id.RecordItem_Name_Type)
}


private fun initRecyclerView() {

}
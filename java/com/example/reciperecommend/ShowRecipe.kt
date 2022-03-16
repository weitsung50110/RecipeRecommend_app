package com.example.reciperecommend

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_record__recipe.*
import kotlinx.android.synthetic.main.activity_show_recipe.*
import java.util.ArrayList

class ShowRecipe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)
        val Recipe_Name = intent.getStringExtra("getSearch")
        ShowRecipe_Name.text = "菜餚名稱：" + Recipe_Name
        ShowRecipe(Recipe_Name)

        ShowRecipe_Close.setOnClickListener(mListener_Close)
        ShowRecipe_Record.setOnClickListener(mListener_GoRecord)
    }

    fun ShowRecipe(Recipe_Name:String)
    {
        val DB_FILE = "Food.db"
        val DB_TABLE_Recipe = "RecipeName"
        val DB_TABLE_Inger = "Inger"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
        MyDB = friDbHp.writableDatabase

        val arrayList_Inger = ArrayList<String>()
        val arrayList_ID = ArrayList<String>()
        val arrayList_rInger = ArrayList<String>()
        val arrayList_rQuan = ArrayList<String>()
        val i = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Inger, arrayOf<String>())
        if (i.count === 0) {
//            Record_Recipe_Inger.text ="沒有資料"
        }
        else {
            i.moveToFirst()
            arrayList_ID.add(i.getString(0))
            arrayList_Inger.add(i.getString(1))

            while (i.moveToNext()) {
                arrayList_ID.add(i.getString(0))
                arrayList_Inger.add(i.getString(1))
            }
        }

        val c = MyDB.rawQuery("SELECT * FROM  " + DB_TABLE_Recipe +" WHERE Name = '" + Recipe_Name + "'", arrayOf<String>())
        if (c.count === 0) {
        }
        else {
            c.moveToFirst()
            ShowRecipe_Vega.text = "葷素："+c.getString(4)
            ShowRecipe_Q.text = "總重量："+c.getString(3)+"克"
            ShowRecipe_Method.text = "作法：\n"+c.getString(5).replace("\\n","\n")
            for (j in arrayList_ID)
            {
                var x:Int = j.toInt()-4
//                Record_Recipe_Inger.append(arrayList_Inger.get(j.toInt()))
                if (c.getString(j.toInt()) != "0" && j.toInt() != 4 && j.toInt() !=5)
                {
                    arrayList_rInger.add(arrayList_Inger.get(x))
                    arrayList_rQuan.add(c.getString(j.toInt()))
                }
            }


        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        Recycle_ShowRecipe.layoutManager = layoutManager
        Recycle_ShowRecipe.adapter = DataAdapter_ShowRecipe(arrayList_rInger,arrayList_rQuan,this)
    }
    private val mListener_Close = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@ShowRecipe, SearchRecipe::class.java)
        startActivity(intent)
    }
    private val mListener_GoRecord = View.OnClickListener {
        val Recipe_Name = intent.getStringExtra("getSearch")
        val intent = Intent(this@ShowRecipe,Record_Recipe::class.java)
        intent.putExtra("getRecipe", Recipe_Name)
        startActivity(intent)
    }
}

class DataAdapter_ShowRecipe(private val iName: List<String>,private val iQuan: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_ShowRecipe>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_ShowRecipe {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_inger, null, false)
        return ViewHolder_ShowRecipe(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_ShowRecipe, position: Int) {
        holder.dataView_Name.text = iName[position]
        holder.dataView_Quan.text = iQuan[position]
        holder.itemView.setOnClickListener {

        }
        holder.itemView.setOnLongClickListener {
//            Toast.makeText(it.context, "Item $position is long clicked.", Toast.LENGTH_SHORT).show()
            true
        }
    }
    override fun getItemCount(): Int {
        return iName.size
    }
}
class ViewHolder_ShowRecipe(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.recipe_inger_name)
    val dataView_Quan: TextView = v.findViewById(R.id.recipe_inger_quantity)
}
private fun initRecyclerView() {
}
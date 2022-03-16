package com.example.reciperecommend

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_record__inger.*
import kotlinx.android.synthetic.main.activity_record__out.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat
import java.util.*

class Record_Out : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record__out)

        val Out_Name = intent.getStringExtra("getOut")
        Record_Out_Name.text = "菜餚名稱："+Out_Name
        UploadOut(Out_Name)

        button_OutSubmit.setOnClickListener(mListener_OutSubmit)
        button_OutCancel.setOnClickListener(mListener_OutCancel)
    }
    private val mListener_OutSubmit = View.OnClickListener {
        Toast.makeText(it.context, "已確認", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setClass(this@Record_Out, History::class.java)
        startActivity(intent)
    }
    private val mListener_OutCancel = View.OnClickListener {
        val Out_Name = intent.getStringExtra("getOut")
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")
        db.collection(GetID.toString())
            .document(Record_Out_Date.text.toString()+ Record_Out_Time.text.toString()+Out_Name)
            .delete()
        Toast.makeText(it.context, "已刪除", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setClass(this@Record_Out, Record::class.java)
        startActivity(intent)
    }
    fun UploadOut(Out_Name:String)
    {
        val t = Calendar.getInstance()
        val year = t.get(Calendar.YEAR)
        val month = t.get(Calendar.MONTH)
        val day = t.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
            { _, year, month, day ->
                t.set(Calendar.YEAR, year)
                t.set(Calendar.MONTH, month)
                t.set(Calendar.DAY_OF_MONTH, day)
                val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                Record_Out_Date.text = Date.format(t.time)

                val options = arrayOf("早餐", "午餐", "晚餐")
                var selectedItem = 0
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Select an option")
                builder.setSingleChoiceItems(options
                    , 0, { dialogInterface: DialogInterface, item: Int ->
                        selectedItem = item
                    })
                builder.setPositiveButton("送出",
                    { dialogInterface: DialogInterface, p1: Int ->
                        Record_Out_Time.text = options[selectedItem]
                        dialogInterface.dismiss()
                        val DB_FILE = "Food.db"
                        val DB_TABLE_Out = "OutName"
                        val MyDB: SQLiteDatabase
                        // 建立自訂的 FriendDbHelper 物件
                        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
                        MyDB = friDbHp.writableDatabase

                        val arrayList_Out = ArrayList<String>()

                        val i = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Out +" WHERE Name = '" + Out_Name + "'", arrayOf<String>())
                        if (i.count === 0) {
//            Record_Recipe_Inger.text ="沒有資料"
                        }
                        else {
                            i.moveToFirst()
                            val RecordOutInput = hashMapOf(
                                "Time" to options[selectedItem],
                                "Date" to Date.format(t.time),
                                "Name" to Out_Name,
                                "Type" to "Out",
                                "熱量" to i.getString(3),
                                "碳水化合物" to i.getString(4),
                                "蛋白質" to i.getString(5),
                                "脂質" to i.getString(6),
                                "全榖雜糧類" to i.getString(7),
                                "豆魚蛋肉類" to i.getString(8),
                                "蔬菜類" to i.getString(9),
                                "水果類" to i.getString(10),
                                "乳品類" to i.getString(11),
                                "油脂與堅果類" to i.getString(12)
                            )
                            val LoginSetting = getSharedPreferences("UserDefault", 0)
                            var GetID = LoginSetting.getString("ID", "尚未登入")
                            db.collection(GetID.toString())
                                .document(Date.format(t.time)+ options[selectedItem]+Out_Name)
                                .set(RecordOutInput)
                            showOut(Out_Name)
                        }
                    })
                builder.create()
                builder.show()
            }, year, month, day).show()


    }

    fun showOut(Out_Name:String)
    {
        val OutCh_Name = ArrayList<String>()
        val OutCh_Quan = ArrayList<String>()
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")

        //顯示field name
        val rootRef = FirebaseFirestore.getInstance()
        val codesRef = rootRef.collection(GetID.toString()).document(Record_Out_Date.text.toString()+ Record_Out_Time.text.toString()+Out_Name)
        codesRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val map = task.result!!.data
                for ((key) in map!!) {
                    if (key == "熱量" || key == "碳水化合物" || key == "蛋白質" || key == "脂質")
                    {
                        OutCh_Name.add(key)
                    }
                }
            }
        }
        //顯示data
        db.collection(GetID.toString())
            .document(Record_Out_Date.text.toString()+ Record_Out_Time.text.toString()+Out_Name)
            .get()
            .addOnSuccessListener { result ->
                for (a in OutCh_Name)
                {
                    val ResultQ :String = result.getString(a).toString()
                    OutCh_Quan.add(ResultQ)
                }
                //將顯示的資料給recyclerview
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                Recycle_Out.layoutManager = layoutManager
                Recycle_Out.adapter = DataAdapter_Out(OutCh_Name,OutCh_Quan,this)
            }
    }
}

class DataAdapter_Out(private val oName: List<String>,private val oQuan: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_Out>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Out {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_inger, null, false)
        return ViewHolder_Out(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_Out, position: Int) {
        holder.dataView_Name.text = oName[position]
        holder.dataView_Quan.text = oQuan[position]
        holder.itemView.setOnClickListener {
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
class ViewHolder_Out(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.recipe_inger_name)
    val dataView_Quan: TextView = v.findViewById(R.id.recipe_inger_quantity)
}
private fun initRecyclerView() {
}
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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_record__inger.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.lang.Math.round
import java.text.SimpleDateFormat
import java.util.*

class Record_Inger : AppCompatActivity() {
    var Calories: Float=0f
    var Carbohydrates: Float=0.00f //碳水化合物
    var protein: Float=0.00f //蛋白質
    var fat: Float=0.00f //脂質
    var Type1: Float=0.00f
    var Type2: Float=0.00f
    var Type3: Float=0.00f
    var Type4: Float=0.00f
    var Type5: Float=0.00f
    var Type6: Float=0.00f
    //1.全榖根莖 2.豆魚蛋肉 3.蔬菜類 4.水果類 5.乳品 6.油脂堅果
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record__inger)

        val Inger_Name = intent.getStringExtra("getInger")
        Record_Inger_Name.text = "食材名稱：" + Inger_Name
//        Record_Inger_Add.setOnClickListener(mListener_IngerAdd)
        button_IngerSubmit.setOnClickListener(mListener_IngerSubmit)
        button_IngerCancel.setOnClickListener(mListener_IngerCancel)
        Record_Inger_g.setOnClickListener(mListener_Ingerg)
        FirstInger(Inger_Name)

    }
    private val mListener_Ingerg = View.OnClickListener {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")
        val Inger_Name = intent.getStringExtra("getInger")
        val item = LayoutInflater.from(this@Record_Inger).inflate(R.layout.inger_change, null)
        AlertDialog.Builder(this@Record_Inger)
            .setTitle("更改克數")
            .setView(item)
            .setPositiveButton("送出") { _, _ ->
                val editText = item.findViewById(R.id.change_quan) as EditText
                db.collection(GetID.toString())
                    .document(Record_Inger_Date.text.toString()+ Record_Inger_Time.text.toString() +Inger_Name)
                    .update(Inger_Name,editText.text.toString())
            }
            .show()
    }
    private val mListener_IngerSubmit = View.OnClickListener {
        Toast.makeText(it.context, "已確認", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setClass(this@Record_Inger, History::class.java)
        startActivity(intent)
    }
    private val mListener_IngerCancel = View.OnClickListener {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")
        val Inger_Name = intent.getStringExtra("getInger")
        db.collection(GetID.toString())
            .document(Record_Inger_Date.text.toString()+ Record_Inger_Time.text.toString()+Inger_Name)
            .delete()
        Toast.makeText(it.context, "已刪除此天食材紀錄", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setClass(this@Record_Inger, Record::class.java)
        startActivity(intent)
    }
    fun FirstInger(Inger_Name:String)
    {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")


            Toast.makeText(this, "今日食材已新增，點選新增增加食材", Toast.LENGTH_SHORT).show()
            val item = LayoutInflater.from(this@Record_Inger).inflate(R.layout.inger_change, null)
            AlertDialog.Builder(this@Record_Inger)
                .setTitle(Inger_Name)
                .setView(item)
                .setPositiveButton("送出") { _, _ ->
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
                            Record_Inger_Date.text = Date.format(t.time)
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
                                    Record_Inger_Time.text = options[selectedItem]
                                    dialogInterface.dismiss()
                                    val editText = item.findViewById(R.id.change_quan) as EditText
                                    val DB_FILE = "Food.db"
                                    val DB_TABLE_Name = "IngerName"
                                    val MyDB: SQLiteDatabase
                                    // 建立自訂的 FriendDbHelper 物件
                                    val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
                                    MyDB = friDbHp.writableDatabase

                                    val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Name+" WHERE Name like '"+Inger_Name+"'", arrayOf<String>())

                                    if (c.count === 0) {
                                        Calories += 0
                                    }
                                    else {
                                        c.moveToFirst()
                                        Calories += round((c.getString(3).toFloat() * editText.text.toString().toFloat() )*100)/100f
                                        Carbohydrates += Math.round(c.getString(4).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        protein += Math.round(c.getString(5).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        fat += Math.round(c.getString(6).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type1 += Math.round(c.getString(7).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type2 += Math.round(c.getString(8).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type3 += Math.round(c.getString(9).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type4 += Math.round(c.getString(10).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type5 += Math.round(c.getString(11).toFloat() * editText.text.toString().toFloat() *100)/100f
                                        Type6 += Math.round((c.getString(12).toFloat() * editText.text.toString().toFloat() )*100)/100f

                                    }
                                    val FirstInger = hashMapOf(
                                        "Date" to Record_Inger_Date.text.toString(),
                                        "Time" to Record_Inger_Time.text.toString(),
                                        "Name" to Inger_Name,
                                        "Type" to "Inger",
                                        Inger_Name to editText.text.toString(),
                                        "熱量" to Calories,
                                        "碳水化合物" to Carbohydrates,
                                        "蛋白質" to protein,
                                        "脂質" to fat,
                                        "全榖雜糧類" to Type1,
                                        "豆魚蛋肉類" to Type2,
                                        "蔬菜類" to Type3,
                                        "水果類" to Type4,
                                        "乳品類" to Type5,
                                        "油脂與堅果類" to Type6
                                    )
                                    db.collection(GetID.toString())
                                        .document(Record_Inger_Date.text.toString()+ Record_Inger_Time.text.toString()+Inger_Name)
                                        .set(FirstInger)

                                    Record_Inger_g.text = "食物克數："+editText.text.toString()
                                    showInger(Date.format(t.time),options[selectedItem],Inger_Name)
                                })
                            builder.create()
                            builder.show();
                        }, year, month, day).show()

                }
                .show()

//        }

//        val intent = Intent()
//        intent.setClass(this@Record_Inger, Record::class.java)
//        startActivity(intent)
    }

    fun showInger(Date: String,Time: String,Inger_Name: String)
    {
        val IngerCh_Name = ArrayList<String>()
        val IngerCh_Quan = ArrayList<String>()
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")

        //顯示field name
        val rootRef = FirebaseFirestore.getInstance()
        val codesRef = rootRef.collection(GetID.toString()).document(Date+ Time+Inger_Name)
        codesRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val map = task.result!!.data
                for ((key) in map!!) {
                    if (key == "熱量" || key == "碳水化合物" || key == "蛋白質" || key == "脂質")
                    {
                        IngerCh_Name.add(key)
                    }
                }
            }
        }
        //顯示data
        db.collection(GetID.toString())
            .document(Date+ Time+Inger_Name)
            .get()
            .addOnSuccessListener { result ->
                for (a in IngerCh_Name)
                {
                    val ResultQ :String = result.get(a).toString()
                    IngerCh_Quan.add(ResultQ)
                }
                //將顯示的資料給recyclerview
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                Recycle_Inger.layoutManager = layoutManager
                Recycle_Inger.adapter = DataAdapter_Inger(Date,Time,Inger_Name,IngerCh_Name,IngerCh_Quan,this)
            }
    }
}

class DataAdapter_Inger(private val Date: String,private val Time: String,private val IngerName: String,private val iName: List<String>,private val iQuan: List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder_Inger>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Inger {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_inger, null, false)
        return ViewHolder_Inger(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_Inger, position: Int) {
        holder.dataView_Name.text = iName[position]
        holder.dataView_Quan.text = iQuan[position]
        holder.itemView.setOnClickListener {
            val item = LayoutInflater.from(context).inflate(R.layout.inger_change, null)
            AlertDialog.Builder(context)
                .setTitle(iName[position])
                .setView(item)
                .setNeutralButton("刪除") { _, _ ->
                    val LoginSetting = context.getSharedPreferences("UserDefault", 0)
                    var GetID = LoginSetting.getString("ID", "尚未登入")
                    db.collection(GetID.toString())
                        .document(Date+ Time+IngerName)
                        .update(holder.dataView_Name.text.toString(), FieldValue.delete())
                }
                .setPositiveButton("送出") { _, _ ->
                    val editText = item.findViewById(R.id.change_quan) as EditText
                    holder.dataView_Quan.text = editText.text.toString()

                    val LoginSetting = context.getSharedPreferences("UserDefault", 0)
                    var GetID = LoginSetting.getString("ID", "尚未登入")
                    db.collection(GetID.toString())
                        .document(Date+ Time+IngerName)
                        .update(holder.dataView_Name.text.toString(),editText.text.toString())
                }
                .show()
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
class ViewHolder_Inger(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.recipe_inger_name)
    val dataView_Quan: TextView = v.findViewById(R.id.recipe_inger_quantity)
}
private fun initRecyclerView() {
}

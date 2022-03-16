package com.example.reciperecommend

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.food_time.*
import kotlinx.coroutines.NonCancellable.cancel
import java.text.SimpleDateFormat
import java.util.*


val db = Firebase.firestore
class Record_Recipe : AppCompatActivity() {
    var Calories: Float=0.00f
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
        setContentView(R.layout.activity_record__recipe)

        val Recipe_Name = intent.getStringExtra("getRecipe")
        Record_Recipe_Recipe.text = "菜餚名稱："+Recipe_Name
        UploadRecipe(Recipe_Name)
        button_recipeSubmit.setOnClickListener(mListener_RecipeSubmit)
        button_recipeCancel.setOnClickListener(mListener_RecipeCancel)
        Record_Recipe_Add.setOnClickListener(mListener_RecipeAdd)
    }
    private val mListener_RecipeSubmit = View.OnClickListener {
        val Recipe_Name = intent.getStringExtra("getRecipe")
        UploadCal(Recipe_Name)
        val intent = Intent()
        intent.setClass(this@Record_Recipe, History::class.java)
        startActivity(intent)
    }
    private val mListener_RecipeCancel = View.OnClickListener {
        val Recipe_Name = intent.getStringExtra("getRecipe")
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")
        db.collection(GetID.toString())
            .document(Record_Recipe_Date.text.toString()+ Record_Recipe_Time.text.toString()+Recipe_Name)
            .delete()
        Toast.makeText(it.context, "已刪除", Toast.LENGTH_SHORT).show()
        val intent = Intent()
        intent.setClass(this@Record_Recipe, Record::class.java)
        startActivity(intent)
    }
    private val mListener_RecipeAdd = View.OnClickListener {
        val DB_FILE = "Food.db"
        val DB_TABLE_Type = "IngerType"
        val DB_TABLE_Name = "IngerName"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
        MyDB = friDbHp.writableDatabase

        val AddInger_Type = ArrayList<String>()
        val AddInger_Name = ArrayList<String>()
//        val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE, arrayOf<String>())
        val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Type, arrayOf<String>())

        if (c.count === 0) {
            AddInger_Type.add("沒有資料")
        }
        else {
            c.moveToFirst()
            AddInger_Type.add(c.getString(1))
            while (c.moveToNext()) {
                AddInger_Type.add(c.getString(1))
            }
        }


        val spinner_type = Spinner(this)
        val arrayadapter_type = ArrayAdapter(this@Record_Recipe, android.R.layout.simple_spinner_item, AddInger_Type)
        spinner_type.adapter = arrayadapter_type



        AlertDialog.Builder(this)
            .setTitle("選擇食材種類")
            .setView(spinner_type)
            .setPositiveButton("選擇") { _, _ ->

                val n = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Name+" WHERE Type = '"+spinner_type.getSelectedItem().toString()+"'", arrayOf<String>())
                if (n.count === 0) {
                    AddInger_Name.add("沒有資料")
                }
                else {
                    n.moveToFirst()
                    AddInger_Name.add(n.getString(2))
                    while (n.moveToNext()) {
                        AddInger_Name.add(n.getString(2))
                    }
                }
                val spinner_name = Spinner(this)
                val arrayadapter_name = ArrayAdapter(this@Record_Recipe, android.R.layout.simple_spinner_item, AddInger_Name)
                spinner_name.adapter = arrayadapter_name

                AlertDialog.Builder(this@Record_Recipe)
                    .setTitle("選擇食材")
                    .setView(spinner_name)
                    .setPositiveButton("選擇") { _, _ ->
                        val Recipe_Name = intent.getStringExtra("getRecipe")
                        val item = LayoutInflater.from(this@Record_Recipe).inflate(R.layout.inger_change, null)
                        AlertDialog.Builder(this@Record_Recipe)
                            .setTitle(spinner_name.getSelectedItem().toString())
                            .setView(item)
                            .setPositiveButton("送出") { _, _ ->
                                val editText = item.findViewById(R.id.change_quan) as EditText
                                val LoginSetting = getSharedPreferences("UserDefault", 0)
                                var GetID = LoginSetting.getString("ID", "尚未登入")
                                db.collection(GetID.toString())
                                    .document(Record_Recipe_Date.text.toString() + Record_Recipe_Time.text.toString()+Recipe_Name)
                                    .update(spinner_name.getSelectedItem().toString(),editText.text.toString())
                                showRecipe(Recipe_Name,Record_Recipe_Date.text.toString(),Record_Recipe_Time.text.toString())
                            }
                            .show()
                    }
                    .show()
        }.show()
    }
    fun getVegan(Recipe_Name:String,Date: String)
    {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")
        db.collection(GetID.toString())
            .document(Date+ Record_Recipe_Time.text.toString()+Recipe_Name)
            .get()
            .addOnSuccessListener { result ->
                Record_Recipe_Vegan.text = "葷素：" + result.getString("葷素")
                Record_Recipe_Method.text = "作法：\n" + result.getString("作法").toString().replace("\\n","\n")
            }
    }

    fun UploadCal(Recipe_Name:String)
    {
        Calories = 0f
        Carbohydrates = 0.00f //碳水化合物
        protein = 0.00f //蛋白質
        fat = 0.00f //脂質

        val IngerCa_Name = ArrayList<String>()
        val IngerCa_Quan = ArrayList<String>()
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")

        //顯示field name
        val rootRef = FirebaseFirestore.getInstance()
        val codesRef = rootRef.collection(GetID.toString()).document(Record_Recipe_Date.text.toString()+ Record_Recipe_Time.text.toString()+Recipe_Name)
        codesRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val map = task.result!!.data
                for ((key) in map!!) {
                    if (key != "Time" && key != "Date" && key != "Name" && key != "Type" && key != "作法" && key != "葷素")
                    {
                        IngerCa_Name.add(key)
                    }
                }
            }
        }
        //顯示data
        db.collection(GetID.toString())
            .document(Record_Recipe_Date.text.toString() + Record_Recipe_Time.text.toString()+Recipe_Name)
            .get()
            .addOnSuccessListener { result ->
                for (a in IngerCa_Name)
                {
                    val ResultQ :String = result.getString(a).toString()
                    IngerCa_Quan.add(ResultQ)

                    val DB_FILE = "Food.db"
                    val DB_TABLE_Name = "IngerName"
                    val MyDB: SQLiteDatabase
                    // 建立自訂的 FriendDbHelper 物件
                    val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
                    MyDB = friDbHp.writableDatabase

                    val c = MyDB.rawQuery("SELECT * FROM  "+DB_TABLE_Name+" WHERE Name like '"+a+"'", arrayOf<String>())

                    if (c.count === 0) {
                        Calories += 0
                    }
                    else {
                        c.moveToFirst()
                        Calories += Math.round(c.getString(3).toFloat() * ResultQ.toFloat() *100)/100f
                        Carbohydrates += Math.round(c.getString(4).toFloat() * ResultQ.toFloat() *100)/100f
                        protein += Math.round(c.getString(5).toFloat() * ResultQ.toFloat() *100)/100f
                        fat += Math.round(c.getString(6).toFloat() * ResultQ.toFloat() *100)/100f
                        Type1 += Math.round(c.getString(7).toFloat() * ResultQ.toFloat() *100)/100f
                        Type2 += Math.round(c.getString(8).toFloat() * ResultQ.toFloat() *100)/100f
                        Type3 += Math.round(c.getString(9).toFloat() * ResultQ.toFloat() *100)/100f
                        Type4 += Math.round(c.getString(10).toFloat() * ResultQ.toFloat() *100)/100f
                        Type5 += Math.round(c.getString(11).toFloat() * ResultQ.toFloat() *100)/100f
                        Type6 += Math.round(c.getString(12).toFloat() * ResultQ.toFloat() *100)/100f

                    }

                }

                val total = mapOf(
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
                //將顯示的資料給recyclerview
                db.collection(GetID.toString())
                    .document(Record_Recipe_Date.text.toString() + Record_Recipe_Time.text.toString()+Recipe_Name)
                    .update(total)

            }
    }
    //將食譜上傳至firebase
    fun UploadRecipe(Recipe_Name:String)
    {
        val item = LayoutInflater.from(this@Record_Recipe).inflate(R.layout.food_quantity, null)
        val item_Time = LayoutInflater.from(this@Record_Recipe).inflate(R.layout.food_time, null)
        AlertDialog.Builder(this@Record_Recipe)
            .setTitle("食用單位：碗(大約200克)")
            .setView(item)
            .setPositiveButton("送出") { _, _ ->
                val t = Calendar.getInstance()
                val year = t.get(Calendar.YEAR)
                val month = t.get(Calendar.MONTH)
                val day = t.get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
                    t.set(Calendar.YEAR, year)
                    t.set(Calendar.MONTH, month)
                    t.set(Calendar.DAY_OF_MONTH, day)
                    val Date = SimpleDateFormat("yyyyMMdd", Locale.TAIWAN)
                    Record_Recipe_Date.text = Date.format(t.time)

                    val options = arrayOf("早餐", "午餐", "晚餐")
                    var selectedItem = 0
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Select an option")
                    builder.setSingleChoiceItems(options
                        , 0, { dialogInterface: DialogInterface, item: Int ->
                            selectedItem = item
                        })
                    builder.setPositiveButton("送出", { dialogInterface: DialogInterface, p1: Int ->
                        Record_Recipe_Time.text = options[selectedItem]
                        dialogInterface.dismiss()

                        val editText_Q = item.findViewById(R.id.food_quan) as EditText
                        val DB_FILE = "Food.db"
                        val DB_TABLE_Recipe = "RecipeName"
                        val DB_TABLE_Inger = "Inger"
                        val MyDB: SQLiteDatabase
                        // 建立自訂的 FriendDbHelper 物件
                        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
                        MyDB = friDbHp.writableDatabase

                        val arrayList_Inger = ArrayList<String>()
                        val arrayList_ID = ArrayList<String>()

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
                        val RecordRecipeInput: HashMap<String,String> = hashMapOf<String,String>("Time" to Record_Recipe_Time.text.toString())
                        RecordRecipeInput.put("Date", Date.format(t.time))
                        RecordRecipeInput.put("Name", Recipe_Name.replace("/",""))
                        RecordRecipeInput.put("Type", "Recipe")
                        val c = MyDB.rawQuery("SELECT * FROM  " + DB_TABLE_Recipe +" WHERE Name = '" + Recipe_Name + "'", arrayOf<String>())
                        if (c.count === 0) {
                        }
                        else {
                            c.moveToFirst()
                            for (j in arrayList_ID)
                            {
                                var x:Int = j.toInt()-4
//                Record_Recipe_Inger.append(arrayList_Inger.get(j.toInt()))
                                if (c.getString(j.toInt()) != "0" && j.toInt() != 4 && j.toInt() !=5)
                                {
                                    var bowl:Float = Math.round(((c.getString(j.toInt()).toFloat() *200) * editText_Q.text.toString().toFloat())/c.getString(3).toFloat())*100/100f
                                    RecordRecipeInput.put(arrayList_Inger.get(x),bowl.toString())
//                            RecordRecipeInput.put(arrayList_Inger.get(x),c.getString(j.toInt()))
                                }
                            }
                            RecordRecipeInput.put("葷素", c.getString(4))
                            RecordRecipeInput.put("作法", c.getString(5))
                            val LoginSetting = getSharedPreferences("UserDefault", 0)
                            var GetID = LoginSetting.getString("ID", "尚未登入")
                            db.collection(GetID.toString())
                                .document(Date.format(t.time)+ Record_Recipe_Time.text.toString()+Recipe_Name)
                                .set(RecordRecipeInput)
                            showRecipe(Recipe_Name,Date.format(t.time),Record_Recipe_Time.text.toString()) //顯示上傳至firebase的食譜
                            getVegan(Recipe_Name,Date.format(t.time))
                        }
                    })
                    builder.create()
                    builder.show();



                }, year, month, day).show()

            }
            .show()

    }

    //顯示上傳至firebase的食譜
    fun showRecipe(Recipe_Name:String,Date:String,Time:String)
    {
        val IngerCh_Name = ArrayList<String>()
        val IngerCh_Quan = ArrayList<String>()
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "尚未登入")

        //顯示field name
        val rootRef = FirebaseFirestore.getInstance()
        val codesRef = rootRef.collection(GetID.toString()).document(Date+ Record_Recipe_Time.text.toString()+Recipe_Name)
        codesRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val map = task.result!!.data
                for ((key) in map!!) {
                    if (key != "Time" && key != "Date" && key != "Name" && key != "Type" && key != "作法" && key != "葷素")
                    {
                        IngerCh_Name.add(key)
                    }
                }
            }
        }
        //顯示data
        db.collection(GetID.toString())
            .document(Date+ Record_Recipe_Time.text.toString()+Recipe_Name)
            .get()
            .addOnSuccessListener { result ->
                for (a in IngerCh_Name)
                {
                    val ResultQ :String = result.getString(a).toString()
                    IngerCh_Quan.add(ResultQ)
                }
                //將顯示的資料給recyclerview
                val layoutManager = LinearLayoutManager(this)
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                Recycle_Recipe.layoutManager = layoutManager
                Recycle_Recipe.adapter = DataAdapter_Recipe(Date,Time,IngerCh_Name,IngerCh_Quan,Recipe_Name,this)
            }
    }
}

class DataAdapter_Recipe(private val Date: String,private val Time: String,private val iName: List<String>,private val iQuan: List<String>,private var Recipe_Name: String, val context: Context) : RecyclerView.Adapter<ViewHolder_Recipe>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder_Recipe {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_inger, null, false)
        return ViewHolder_Recipe(v)
    }
    override fun onBindViewHolder(holder: ViewHolder_Recipe, position: Int) {
        holder.dataView_Name.text = iName[position]
        holder.dataView_Quan.text = iQuan[position]
        holder.itemView.setOnClickListener {
            val item = LayoutInflater.from(context).inflate(R.layout.inger_change, null)
            val here = LayoutInflater.from(context).inflate(R.layout.activity_record__recipe, null)
            AlertDialog.Builder(context)
                .setTitle(iName[position])
                .setView(item)
                .setPositiveButton("送出") { _, _ ->
                    val editText = item.findViewById(R.id.change_quan) as EditText
                    holder.dataView_Quan.text = editText.text.toString()

                    val LoginSetting = context.getSharedPreferences("UserDefault", 0)
                    var GetID = LoginSetting.getString("ID", "尚未登入")
                    db.collection(GetID.toString())
                        .document(Date+ Time+Recipe_Name)
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
class ViewHolder_Recipe(v: View) : RecyclerView.ViewHolder(v) {
    val dataView_Name: TextView = v.findViewById(R.id.recipe_inger_name)
    val dataView_Quan: TextView = v.findViewById(R.id.recipe_inger_quantity)
}
private fun initRecyclerView() {
}


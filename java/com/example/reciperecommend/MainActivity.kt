package com.example.reciperecommend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_record__recipe.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //先將Out.db的資料表匯入
        val db_Out = File(getDatabasePath("Food.db").getPath()) //Get the file name of the database
        val dbdir_Out: File = db_Out.getParentFile()
        if (!dbdir_Out.exists()) {
            dbdir_Out.mkdirs()
        }
        //执行数据库导入
        val myInput_Out = applicationContext.assets.open("Food.db") //assets.open("ingerdients.db") //欲导入的数据库
        val myOutput_Out  = FileOutputStream(db_Out)
        val buffer_Out = ByteArray(1024)
        var length_Out: Int = myInput_Out.read(buffer_Out)
        while ((length_Out) > 0) {
            myOutput_Out.write(buffer_Out, 0, length_Out)
            length_Out = myInput_Out.read(buffer_Out)
        }
        myOutput_Out.flush()
        myInput_Out.close()
        myOutput_Out.close()



        val db = Firebase.firestore
        db.collection("user_info")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user_ID = document.getString("ID")
                    val LoginSetting = getSharedPreferences("UserDefault", 0)
                    var GetID = LoginSetting.getString("ID", null)

                    if (user_ID.equals(GetID))
                    {
                        val intent = Intent()
                        intent.setClass(this@MainActivity, Main2Activity::class.java)
                        startActivity(intent)
                        break
                    }
                    else{
//                        textView2.text = "尚未登入"
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
        button_Main_Login.setOnClickListener(mListener_SignIn)
    }
    private val mListener_SignIn = View.OnClickListener {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        LoginSetting.edit()
            .putString("ID", "SignOut")
            .putString("Password", "SignOut")
            .commit()
        var GetID = LoginSetting.getString("ID", "no value")
        val intent = Intent()
        intent.setClass(this@MainActivity, Login::class.java)
        startActivity(intent);
    }
}
fun getTime():String {
    var calender = Calendar.getInstance()
    var Time:String = calender.get(Calendar.HOUR_OF_DAY).toString()
    if (Time < "11" && Time > "0")
    {
        return "早餐"
    }
    else if (Time <= "16" && Time >= "11")
    {
        return "午餐"
    }
    else
    {
        return "晚餐"
    }
}

fun setDateFormat(year: Int, month: Int, day: Int): String {
    return "$year-${month + 1}-$day"
}

fun getNow(): String {
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    return "${setDateFormat(year, month, day)}"
}

fun getDate(): String{
    val sDateFormat = SimpleDateFormat("yyyyMMdd")
    val date = sDateFormat.format(Date())
    return date
}


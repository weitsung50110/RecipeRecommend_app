package com.example.reciperecommend

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.telecom.Call
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import java.io.IOException
import java.util.*


class SignUp : AppCompatActivity() {
    val db = Firebase.firestore
    var Calories:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //顯示現在時間
        textView_SignUp_Birthday.text = getNow()
        //設定身高NumberPicker
        picker_SignUp_Height.minValue = 100
        picker_SignUp_Height.maxValue = 200
        picker_SignUp_Height.setValue(160)
        //設定體重NumberPicker
        picker_SignUp_Weight.minValue = 30
        picker_SignUp_Weight.maxValue = 200
        picker_SignUp_Weight.setValue(50)

        button_SignUp_Submit.setOnClickListener(mListener_SignUpSubmit)
        button_SignUp_CheckID.setOnClickListener(mListener_CheckID)
        textView_SignUp_Birthday.setOnClickListener(mListener_Birthday)
    }
    private val mListener_CheckID = View.OnClickListener {
        db.collection("user_info")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
                    val user_ID = document.getString("ID")
                    if (editText_SignUp_ID.text.toString()!="")
                    {
                        if (user_ID.equals(editText_SignUp_ID.text.toString())) //判斷ID是否重複
                        {
                            button_SignUp_CheckID.text = "此ID已存在"
                            val toast = Toast.makeText(this, "此ID已存在", Toast.LENGTH_LONG).show()
                            break
                        }
                        else
                        {
                            button_SignUp_CheckID.text = "此ID可使用"
                        }
                    }
                    else
                    {
                        val toast = Toast.makeText(this, "ID不可空白", Toast.LENGTH_LONG).show()
                    }

                }
            }
    }
    private val mListener_SignUpSubmit = View.OnClickListener {
        //取得radioBox的內容
        val radioS: RadioButton = findViewById(radioGroup_Sex.checkedRadioButtonId)
        val radioV: RadioButton = findViewById(radioGroup_Vege.checkedRadioButtonId)
        val radioE: RadioButton = findViewById(radioGroup_Exercise.checkedRadioButtonId)

        if (button_SignUp_CheckID.text == "此ID可使用")
        {
            if (editText_SignUp_ID.text.toString().equals(editText_SignUp_Confirm.text.toString()))
            {
                var Height:Float = picker_SignUp_Height.getValue().toString().toFloat()
                var Weight:Int = picker_SignUp_Weight.getValue().toString().toInt()
                var Exercise:String = radioE.text.toString()
                //將註冊資訊存入firebase
                val user = hashMapOf(
                    "ID" to editText_SignUp_ID.text.toString(),
                    "Password" to editText_SignUp_Password.text.toString(),
                    "Name" to editText_SignUp_Name.text.toString(),
                    "Email" to editText_SignUp_Email.text.toString(),
                    "Sex" to radioS.text.toString(),
                    "Birthday" to textView_SignUp_Birthday.text.toString(),
                    "Height" to picker_SignUp_Height.getValue().toString(),
                    "Weight" to picker_SignUp_Weight.getValue().toString(),
                    "Vege" to radioV.text.toString(),
                    "Exercise" to radioE.text.toString(),
                    "Calories" to getCal(Height,Weight,Exercise).toString()
                )
                if (radioV.text.toString().equals("葷"))
                {
                    if (getCal(Height,Weight,Exercise) <= 1200)
                    {
                        user.put("全榖雜糧類","1.5")
                        user.put("豆魚蛋肉類","3")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","4")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1200 && getCal(Height,Weight,Exercise) <= 1500)
                    {
                        user.put("全榖雜糧類","2.5")
                        user.put("豆魚蛋肉類","4")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","4")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1500 && getCal(Height,Weight,Exercise) <= 1800)
                    {
                        user.put("全榖雜糧類","3")
                        user.put("豆魚蛋肉類","5")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","5")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1800 && getCal(Height,Weight,Exercise) <= 2000)
                    {
                        user.put("全榖雜糧類","3")
                        user.put("豆魚蛋肉類","6")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","4")
                        user.put("水果類","3")
                        user.put("油脂與堅果類","6")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2000 && getCal(Height,Weight,Exercise) <= 2200)
                    {
                        user.put("全榖雜糧類","3.5")
                        user.put("豆魚蛋肉類","6")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","4")
                        user.put("水果類","3.5")
                        user.put("油脂與堅果類","6")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2200 && getCal(Height,Weight,Exercise) <= 2500)
                    {
                        user.put("全榖雜糧類","4")
                        user.put("豆魚蛋肉類","7")
                        user.put("乳品類","1.5")
                        user.put("蔬菜類","5")
                        user.put("水果類","4")
                        user.put("油脂與堅果類","7")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2500)
                    {
                        user.put("全榖雜糧類","4")
                        user.put("豆魚蛋肉類","8")
                        user.put("乳品類","2")
                        user.put("蔬菜類","5")
                        user.put("水果類","4")
                        user.put("油脂與堅果類","8")
                    }
                }
                else if (radioV.text.toString().equals("素"))
                {
                    if (getCal(Height,Weight,Exercise) <= 1200)
                    {
                        user.put("全榖雜糧類","1.5")
                        user.put("豆魚蛋肉類","4.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","4")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1200 && getCal(Height,Weight,Exercise) <= 1500)
                    {
                        user.put("全榖雜糧類","2.5")
                        user.put("豆魚蛋肉類","5.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","4")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1500 && getCal(Height,Weight,Exercise) <= 1800)
                    {
                        user.put("全榖雜糧類","3")
                        user.put("豆魚蛋肉類","6.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","3")
                        user.put("水果類","2")
                        user.put("油脂與堅果類","5")
                    }
                    else if (getCal(Height,Weight,Exercise) > 1800 && getCal(Height,Weight,Exercise) <= 2000)
                    {
                        user.put("全榖雜糧類","3")
                        user.put("豆魚蛋肉類","7.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","4")
                        user.put("水果類","3")
                        user.put("油脂與堅果類","6")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2000 && getCal(Height,Weight,Exercise) <= 2200)
                    {
                        user.put("全榖雜糧類","3.5")
                        user.put("豆魚蛋肉類","7.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","4")
                        user.put("水果類","3.5")
                        user.put("油脂與堅果類","6")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2200 && getCal(Height,Weight,Exercise) <= 2500)
                    {
                        user.put("全榖雜糧類","4")
                        user.put("豆魚蛋肉類","8.5")
                        user.put("乳品類","0")
                        user.put("蔬菜類","5")
                        user.put("水果類","4")
                        user.put("油脂與堅果類","7")
                    }
                    else if (getCal(Height,Weight,Exercise) > 2500)
                    {
                        user.put("全榖雜糧類","4")
                        user.put("豆魚蛋肉類","10")
                        user.put("乳品類","0")
                        user.put("蔬菜類","5")
                        user.put("水果類","4")
                        user.put("油脂與堅果類","8")
                    }
                }
                // Add a new document with a generated ID
                db.collection("user_info")
                    .document(editText_SignUp_ID.text.toString())
                    .set(user)

                //註冊完後自動跳回登入頁面
                val intent = Intent()
                intent.setClass(this@SignUp, Login::class.java)
                startActivity(intent)
            }
        }
        else
        {
            val toast = Toast.makeText(this, "請先檢查ID", Toast.LENGTH_LONG).show()
        }

    }

    private val mListener_Birthday = View.OnClickListener {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, { _, year, month, day ->
            run {
                val format = "${setDateFormat(year, month, day)}"
                textView_SignUp_Birthday.text = format
            }
        }, year, month, day).show()
    }


    fun getCal(Height:Float, Weight: Int, Exercise:String):Int {

        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value")



        var BMI:Float = Math.round((Weight.toFloat() / ((Height/100) * (Height/100))) * 100) /100f
        if (BMI <18.5)
        {
            if (Exercise == "輕度運動")
            {
                Calories = Weight * 35
            }
            else if (Exercise == "中度運動")
            {
                Calories = Weight * 40
            }
            else if (Exercise == "重度運動")
            {
                Calories = Weight * 45
            }

        }
        else if (BMI >=18.5 && BMI <24)
        {
            if (Exercise == "輕度運動")
            {
                Calories = Weight * 30
            }
            else if (Exercise == "中度運動")
            {
                Calories = Weight * 35
            }
            else if (Exercise == "重度運動")
            {
                Calories = Weight * 40
            }
        }
        else if (BMI >= 24)
        {
            if (Exercise == "輕度運動")
            {
                Calories = Weight * 25
            }
            else if (Exercise == "中度運動")
            {
                Calories = Weight * 30
            }
            else if (Exercise == "重度運動")
            {
                Calories = Weight * 35
            }
        }

        return Calories
    }
}

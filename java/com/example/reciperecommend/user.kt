package com.example.reciperecommend

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_record__recipe.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_user.*


class user : AppCompatActivity() {
    var Calories:Float = 0f
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        getUserValue()

        User_Height.minValue = 100
        User_Height.maxValue = 200

        User_Weight.minValue = 30
        User_Weight.maxValue = 200

        User_Refresh.setOnClickListener(mListener_Refresh)
        User_Input.setOnClickListener(mListener_Input)

        User_Cancel.setOnClickListener(mListener_Cancel)
        User_Submit.setOnClickListener(mListener_Submit)
    }

    fun getUserValue()
    {
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        db.collection("user_info")
            .document(GetID)
            .get()
            .addOnSuccessListener { result ->
                //ID
                User_ID.text = result.getString("ID")
                //名字
                User_Name.text = result.getString("Name")

                //性別
                if (result.getString("Sex").equals("Male"))
                {
                    User_Male.setChecked(true)
                }
                else if (result.getString("Sex").equals("Female"))
                {
                    User_Female.setChecked(true)
                }

                //Email
                User_Email.setText(result.getString("Email"))

                //葷素
                if (result.getString("Vege").equals("葷"))
                {
                    User_Meat.setChecked(true)
                }
                else if (result.getString("Vege").equals("素"))
                {
                    User_Vega.setChecked(true)
                }

                //身高體重預設值
                User_Height.setValue(result.get("Height").toString().toInt())
                User_Weight.setValue(result.get("Weight").toString().toInt())

                //運動量
                if (result.getString("Exercise").equals("輕度運動"))
                {
                    User_E_L.setChecked(true)
                }
                else if (result.getString("Exercise").equals("中度運動"))
                {
                    User_E_M.setChecked(true)
                }
                else if (result.getString("Exercise").equals("重度運動"))
                {
                    User_E_H.setChecked(true)
                }
                //熱量預設值
                User_Calories.setText(result.get("Calories").toString())
                User_Calories.setInputType(InputType.TYPE_NULL)

                User_Type1.setText(result.get("全榖雜糧類").toString())
                User_Type1.setInputType(InputType.TYPE_NULL)

                User_Type2.setText(result.get("豆魚蛋肉類").toString())
                User_Type2.setInputType(InputType.TYPE_NULL)

                User_Type3.setText(result.get("蔬菜類").toString())
                User_Type3.setInputType(InputType.TYPE_NULL)

                User_Type4.setText(result.get("水果類").toString())
                User_Type4.setInputType(InputType.TYPE_NULL)

                User_Type5.setText(result.get("乳品類").toString())
                User_Type5.setInputType(InputType.TYPE_NULL)

                User_Type6.setText(result.get("油脂與堅果類").toString())
                User_Type6.setInputType(InputType.TYPE_NULL)


            }
    }
    private val mListener_Refresh = View.OnClickListener {

        var Height:Float = User_Height.getValue().toString().toFloat() / 100
        var Weight:Float = User_Weight.getValue().toString().toFloat()

        var BMI:Float = Math.round((Weight / (Height * Height)) * 100) /100f

        val radioE: RadioButton = findViewById(User_E.checkedRadioButtonId)
        val radioV: RadioButton = findViewById(User_V.checkedRadioButtonId)
        var Exercise :String = radioE.text.toString()
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

        User_Calories.setText(Calories.toString())

        if (radioV.text.toString().equals("葷"))
        {
            if (Calories <= 1200)
            {
                User_Type1.setText("1.5")
                User_Type2.setText("3")
                User_Type5.setText("1.5")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("4")
            }
            else if (Calories > 1200 && Calories <= 1500)
            {
                User_Type1.setText("2.5")
                User_Type2.setText("4")
                User_Type5.setText("1.5")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("4")
            }
            else if (Calories > 1500 && Calories <= 1800)
            {
                User_Type1.setText("3")
                User_Type2.setText("5")
                User_Type5.setText("1.5")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("5")
            }
            else if (Calories > 1800 && Calories <= 2000)
            {
                User_Type1.setText("3")
                User_Type2.setText("6")
                User_Type5.setText("1.5")
                User_Type3.setText("4")
                User_Type4.setText("3")
                User_Type6.setText("6")
            }
            else if (Calories > 2000 && Calories <= 2200)
            {
                User_Type1.setText("3.5")
                User_Type2.setText("6")
                User_Type5.setText("1.5")
                User_Type3.setText("4")
                User_Type4.setText("3.5")
                User_Type6.setText("6")
            }
            else if (Calories > 2200 && Calories <= 2500)
            {
                User_Type1.setText("4")
                User_Type2.setText("7")
                User_Type5.setText("1.5")
                User_Type3.setText("5")
                User_Type4.setText("4")
                User_Type6.setText("7")
            }
            else if (Calories > 2500)
            {
                User_Type1.setText("4")
                User_Type2.setText("8")
                User_Type5.setText("2")
                User_Type3.setText("5")
                User_Type4.setText("4")
                User_Type6.setText("8")
            }
        }
        else if (radioV.text.toString().equals("素"))
        {
            if (Calories <= 1200)
            {
                User_Type1.setText("1.5")
                User_Type2.setText("4.5")
                User_Type5.setText("0")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("4")
            }
            else if (Calories > 1200 && Calories <= 1500)
            {
                User_Type1.setText("2.5")
                User_Type2.setText("5.5")
                User_Type5.setText("0")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("4")
            }
            else if (Calories > 1500 && Calories <= 1800)
            {
                User_Type1.setText("3")
                User_Type2.setText("6.5")
                User_Type5.setText("0")
                User_Type3.setText("3")
                User_Type4.setText("2")
                User_Type6.setText("5")
            }
            else if (Calories > 1800 && Calories <= 2000)
            {
                User_Type1.setText("3")
                User_Type2.setText("7.5")
                User_Type5.setText("0")
                User_Type3.setText("4")
                User_Type4.setText("3")
                User_Type6.setText("6")
            }
            else if (Calories > 2000 && Calories <= 2200)
            {
                User_Type1.setText("3.5")
                User_Type2.setText("7.5")
                User_Type5.setText("0")
                User_Type3.setText("4")
                User_Type4.setText("3.5")
                User_Type6.setText("6")
            }
            else if (Calories > 2200 && Calories <= 2500)
            {
                User_Type1.setText("4")
                User_Type2.setText("8.5")
                User_Type5.setText("0")
                User_Type3.setText("5")
                User_Type4.setText("4")
                User_Type6.setText("7")
            }
            else if (Calories > 2500)
            {
                User_Type1.setText("4")
                User_Type2.setText("10")
                User_Type5.setText("0")
                User_Type3.setText("5")
                User_Type4.setText("4")
                User_Type6.setText("8")
            }
        }
    }
    private val mListener_Input = View.OnClickListener {
        User_Calories.setInputType(InputType.TYPE_CLASS_NUMBER)
//        User_Type1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)

        val digists = "0123456789." //設定可以輸入的字
        User_Type1.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type1.filters = arrayOf(MoneyInputFilter())
        User_Type2.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type2.filters = arrayOf(MoneyInputFilter())
        User_Type3.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type3.filters = arrayOf(MoneyInputFilter())
        User_Type4.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type4.filters = arrayOf(MoneyInputFilter())
        User_Type5.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type5.filters = arrayOf(MoneyInputFilter())
        User_Type6.setKeyListener(DigitsKeyListener.getInstance(digists))
        User_Type6.filters = arrayOf(MoneyInputFilter())
    }

    private val mListener_Cancel = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this@user, Main2Activity::class.java)
        startActivity(intent)
    }
    private val mListener_Submit = View.OnClickListener {
        val radioS: RadioButton = findViewById(User_Sex.checkedRadioButtonId)
        val radioV: RadioButton = findViewById(User_V.checkedRadioButtonId)
        val radioE: RadioButton = findViewById(User_E.checkedRadioButtonId)

        val UserUpdate = mapOf(
            "Sex" to radioS.text.toString(),
            "Email" to User_Email.text.toString(),
            "Vege" to radioV.text.toString(),
            "Height" to User_Height.getValue().toString(),
            "Weight" to User_Weight.getValue().toString(),
            "Exercise" to radioE.text.toString(),
            "Calories" to User_Calories.text.toString(),
            "全榖雜糧類" to User_Type1.text.toString(),
            "豆魚蛋肉類" to User_Type2.text.toString(),
            "蔬菜類" to User_Type3.text.toString(),
            "水果類" to User_Type4.text.toString(),
            "乳品類" to User_Type5.text.toString(),
            "油脂與堅果類" to User_Type6.text.toString()
        )
        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var GetID = LoginSetting.getString("ID", "no value").toString()
        db.collection("user_info")
            .document(GetID)
            .update(UserUpdate)

        LoginSetting.edit()
            .putString("Calories", User_Calories.text.toString())
            .commit()
        Toast.makeText(this, "已更新", Toast.LENGTH_LONG).show()

        val intent = Intent()
        intent.setClass(this@user, Main2Activity::class.java)
        startActivity(intent)
    }
}

//限制小數點
class MoneyInputFilter : InputFilter {
    companion object {
        val POINT_LENGTH = 2//保留小数点位数
    }

    override fun filter(
        source: CharSequence,//将要输入的字符串,如果是删除操作则为空
        start: Int,//将要输入的字符串起始下标，一般为0
        end: Int,//start + source字符的长度
        dest: Spanned,//输入之前文本框中的内容
        dstart: Int,//将会被替换的起始位置
        dend: Int//dstart+将会被替换的字符串长度
    ): CharSequence {//方法返回的值将会替换掉dest字符串中dstartd位置到dend位置之间字符，返回source表示不做任何处理，返回空字符串""表示不输入任何字符
//        Log.d("MoneyInputFilter", "[source:$source][start:$start][end:$end][dest:$dest][dstart:$dstart][dend:$dend]")

        val start = dest.subSequence(0, dstart)
        val end = dest.subSequence(dend, dest.length)
        val target = start.toString() + source + end//字符串变化后的结果
        val backup = dest.subSequence(dstart, dend)//将要被替换的字符串

        if (target.indexOf(".") == 0) {//不允许第一个字符为.
            return backup
        }

        if (target.startsWith("0") && !target.startsWith("0.") && "0" != target) {//不允许出现0123、0456这类字符串
            return backup
        }

        //限制小数点后面只能有两位小数
        val index = target.indexOf(".")
        if (index >= 0 && index + POINT_LENGTH + 2 <= target.length) {
            return backup
        }

        return source
    }
}
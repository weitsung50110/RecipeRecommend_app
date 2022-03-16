package com.example.reciperecommend

import android.app.ProgressDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_recommand.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class Recommand : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommand)


        var qq:Int=0 //避免出去回來之後,直又會改變
        //val PACKAGE_NAME = "com.example.last_win"//工程包名
        //val DB_PATH = "/data/data/$PACKAGE_NAME/databases"  //在手机里存放数据库的位置
        //val dbfile = "$DB_PATH/ingerdients.db"

        val db = File(getDatabasePath("Food.db").getPath()) //Get the file name of the database
        val dbdir: File = db.getParentFile()
        if (!dbdir.exists()) {
            dbdir.mkdirs()
        }
        //Toast.makeText(this, getDatabasePath("Food.db").getPath(), Toast.LENGTH_LONG).show()

        //执行数据库导入
        val myInput = applicationContext.assets.open("Food.db") //assets.open("ingerdients.db") //欲导入的数据库
        val myOutput  = FileOutputStream(db)


        val buffer = ByteArray(1024)
        var length: Int = myInput.read(buffer)
        while ((length) > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }
        myOutput.flush()
        myInput.close()
        myOutput.close()


        val LoginSetting = getSharedPreferences("UserDefault", 0)
        var Calories = LoginSetting.getString("Calories","").toString().toDouble()
        var Vege = LoginSetting.getString("Vege","").toString()
        var Login_Recipe_type1 = LoginSetting.getString("Login_Recipe_type1","").toString()
        var Login_Recipe_type2 = LoginSetting.getString("Login_Recipe_type2","").toString()
        var Login_Recipe_type3 = LoginSetting.getString("Login_Recipe_type3","").toString()
        var Login_Recipe_type4 = LoginSetting.getString("Login_Recipe_type4","").toString()
        var Login_Recipe_type5 = LoginSetting.getString("Login_Recipe_type5","").toString()
        var Login_Recipe_type6 = LoginSetting.getString("Login_Recipe_type6","").toString()
        //time_text4.text=Login_Recipe_type1+" "+Login_Recipe_type2+" "+Login_Recipe_type3+" "+
        //Login_Recipe_type4+" "+Login_Recipe_type5+" "+Login_Recipe_type6


        val LoginSetting2 = getSharedPreferences("Have_eaten", 0) //寫在Recommand_0那邊,之後合併後要自己加上去
        var Mor_Eaten_Calories = LoginSetting2.getString("Mor_Eaten_Calories","0").toString().toDouble()
        var Noon_Eaten_Calories = LoginSetting2.getString("Noon_Eaten_Calories","0").toString().toDouble()
        var Dinner_Eaten_Calories = LoginSetting2.getString("Dinner_Eaten_Calories","0").toString().toDouble()
        var Mor_Eaten_Count = LoginSetting2.getString("Mor_Count","0")
        var Noon_Eaten_Count = LoginSetting2.getString("Noon_Count","0")
        var Dinner_Eaten_Count = LoginSetting2.getString("Dinner_Count","0")

        val LoginSetting3 = getSharedPreferences("Fixed", 0)
        if(LoginSetting3.getString("Mor_fuck_you","0")=="fuck"){
            LoginSetting3.edit()
                .clear()
                .commit()
        }
        if(LoginSetting3.getString("Noon_fuck_you","0")=="fuck"){
            LoginSetting3.edit()
                .clear()
                .commit()
        }
        if(LoginSetting3.getString("Dinner_fuck_you","0")=="fuck"){
            LoginSetting3.edit()
                .clear()
                .commit()
        }



        //textView4.text = Dinner_Eaten_Calories.toString()
        Mor_eaten_count_text.text = "已吃"+Mor_Eaten_Count+"筆"
        Noon_eaten_count_text.text = "已吃"+Noon_Eaten_Count+"筆"
        Dinner_eaten_count_text.text = "已吃"+Dinner_Eaten_Count+"筆"

        //refresh_button
        button_refresh.setOnClickListener(mListener_refresh)
        button_clean.setOnClickListener(mListener_clean)
        button_back.setOnClickListener(mListener_back)

        val arrayList_main = ArrayList<recommand_model>()
        val arrayList_main2 = ArrayList<recommand_model>()
        val arrayList_main3 = ArrayList<recommand_model>()
        val arrayList_no = ArrayList<no_model>()
        val arrayList_no2 = ArrayList<no_model>()
        val arrayList_no3 = ArrayList<no_model>()

        val DB_FILE = "Food.db"
        val DB_TABLE = "RecipeName"
        val MyDB: SQLiteDatabase
        // 建立自訂的 FriendDbHelper 物件
        val friDbHp = MyDBHelper(applicationContext, DB_FILE, null, 1)
        // 設定建立 table 的指令

        // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
        MyDB = friDbHp.writableDatabase


        val c = MyDB.rawQuery("select * from "+DB_TABLE , null )

        if (c.count == 0) {
            Toast.makeText(this, "c沒有資料", Toast.LENGTH_SHORT).show()
        }
        else {
            val DB_FILE2 = "Food.db"
            val DB_TABLE2 = "IngerName"
            val MyDB2: SQLiteDatabase
            // 建立自訂的 FriendDbHelper 物件
            val friDbHp2 = MyDBHelper(applicationContext, DB_FILE2, null, 1)
            // 設定建立 table 的指令

            // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
            MyDB2 = friDbHp2.writableDatabase


            var time_detail1 = getTime_detail()
            //Date_text.text = getDate_detail()//得到 小時/分鐘/秒數
            var time1 = getTime() //得到小時

            var i=5
            var judge:Double=0.0 //判斷
            var total:Double=0.0
            var Carbohydrates:Double=0.0
            var protein:Double=0.0
            var fat:Double=0.0
            var count:Int=0
            var Recipe_type:String=""
            var Recipe_type1:Double=0.0
            var Recipe_type2:Double=0.0
            var Recipe_type3:Double=0.0
            var Recipe_type4:Double=0.0
            var Recipe_type5:Double=0.0
            var Recipe_type6:Double=0.0
            var random:Int =(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
            var Mor_random:Int =(Math.random()*117).toInt()+1 //早餐專用

            var All_calories:Double= 0.0
            var All_Recipe_type1:Double= 0.0
            var All_Recipe_type2:Double= 0.0
            var All_Recipe_type3:Double= 0.0
            var All_Recipe_type4:Double= 0.0
            var All_Recipe_type5:Double= 0.0
            var All_Recipe_type6:Double= 0.0



            time_text.text="早餐"
            qq=0

            var Mor_Calories = (Calories * 0.3 )
            judge= Mor_Calories * 0.38

            c.moveToFirst()
            count=0
            var Mor_fixed = LoginSetting3.getString("Mor_fixed0","NULL")
            if(Mor_fixed!="NULL"){
                Mor_random = Mor_fixed.toString().toInt()
            }
            else{
                Mor_Calories = (Calories * 0.3 )- Mor_Eaten_Calories
                judge= Mor_Calories * 0.38
                qq=1
            }

            while (c.moveToPosition(Mor_random)) {
                /*if(Vege!="葷"&&c.getString(4)=="葷"){
                    Mor_random = (Math.random()*117).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                    continue
                }*/


                total=0.0
                Carbohydrates=0.0
                protein=0.0
                fat=0.0
                i=5
                count++

                Recipe_type1=0.0
                Recipe_type2=0.0
                Recipe_type3=0.0
                Recipe_type4=0.0
                Recipe_type5=0.0
                Recipe_type6=0.0


                while(i<=1113)
                {
                    if(c.getString(i)!="0")
                    {

                        val q = MyDB2.rawQuery("select * from "+DB_TABLE2+" where Name ='"+c.getColumnName(i)+"'", null )

                        if (q.count == 0) {
                            //Toast.makeText(this, "q沒有資料", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            q.moveToFirst()
                            total=(q.getString(3).toDouble()*c.getString(i).toDouble())+total
                            Carbohydrates=(q.getString(4).toDouble()*c.getString(i).toDouble())+Carbohydrates
                            protein=(q.getString(5).toDouble()*c.getString(i).toDouble())+protein
                            fat=(q.getString(6).toDouble()*c.getString(i).toDouble())+fat

                            if(q.getString(7)!="0"){
                                Recipe_type1=Recipe_type1+(q.getString(7).toDouble())*c.getString(i).toDouble()
                                //Recipe_type=q.getColumnName(7)
                            }
                            else if(q.getString(8)!="0"){
                                Recipe_type2=Recipe_type2+(q.getString(8).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(9)!="0"){
                                Recipe_type3=Recipe_type3+(q.getString(9).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(10)!="0"){
                                Recipe_type4=Recipe_type4+(q.getString(10).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(11)!="0"){
                                Recipe_type5=Recipe_type5+(q.getString(11).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(12)!="0"){
                                Recipe_type6=Recipe_type6+(q.getString(12).toDouble())*c.getString(i).toDouble()
                            }
                            q.close()
                        }
                    }
                    i++
                }
                if(total<=Mor_Calories&&Recipe_type1<1&&Recipe_type2<1&&Recipe_type6<1){

                    All_calories = All_calories + total
                    All_Recipe_type1 = All_Recipe_type1 + Recipe_type1
                    All_Recipe_type2 = All_Recipe_type2 + Recipe_type2
                    All_Recipe_type3 = All_Recipe_type3 + Recipe_type3
                    All_Recipe_type4 = All_Recipe_type4 + Recipe_type4
                    All_Recipe_type5 = All_Recipe_type5 + Recipe_type5
                    All_Recipe_type6 = All_Recipe_type6 + Recipe_type6

                    arrayList_main.add(recommand_model(c.getString(0),c.getString(1),c.getString(2),c.getString(4),
                        Decimal_point(total),Decimal_point(Carbohydrates),Decimal_point(protein),Decimal_point(fat)
                        ,Decimal_point(Recipe_type1),Decimal_point(Recipe_type2),Decimal_point(Recipe_type3),
                        Decimal_point(Recipe_type4),Decimal_point(Recipe_type5),Decimal_point(Recipe_type6),
                        c.getString(5),getDate(),"早餐"))
                    //Decimal_point 取小數點後兩位
                    Mor_Calories = Mor_Calories - total

                    if(Mor_fixed=="NULL"){
                        LoginSetting3.edit()
                            .putString("Mor_fixed"+(count-1), Mor_random.toString())  //在第65列
                            .commit()
                    }

                    if(qq==0 && LoginSetting3.getString("Mor_Count_fixed","")==count.toString()){
                        break
                    }

                }
                else{
                    count=count-1
                    if(Mor_Calories<=200){
                        break
                    }
                    else if(Mor_Calories < judge){
                        break
                    }
                }

                Mor_fixed = LoginSetting3.getString("Mor_fixed"+(count),"NULL")
                if(Mor_fixed=="NULL"){
                    Mor_random = (Math.random()*117).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                }
                else{
                    Mor_random = Mor_fixed.toString().toInt()
                }

                //Mor_random = (Math.random()*117).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
            }
            if(count==0){//若Count=0顯示的東西
                arrayList_no.add(no_model("No","你已經吃超量了!"))
                val myAdapter_main = no_adapter(arrayList_no,this)
                rec_recyclerView1.layoutManager= LinearLayoutManager(this)
                rec_recyclerView1.adapter = myAdapter_main

                LoginSetting3.edit()
                    .putString("Mor_fuck_you", "fuck")
                    .commit()
            }
            else{
                val myAdapter_main = recommand_adapter(arrayList_main,this)
                rec_recyclerView1.layoutManager= LinearLayoutManager(this)
                rec_recyclerView1.adapter = myAdapter_main
            }
            if(qq==1){
                LoginSetting3.edit()
                    .putString("Mor_Count_fixed", count.toString())
                    .commit()
            }
            Mor_count_text.text = "有" + count.toString() + "筆"




            time_text2.text="午餐"
            qq = 0

            var Noon_Calories = (Calories * 0.4)
            judge=Noon_Calories*0.38


            c.moveToFirst()
            count=0
            var Noon_fixed = LoginSetting3.getString("Noon_fixed0","NULL")
            if(Noon_fixed!="NULL"){
                random = Noon_fixed.toString().toInt()
            }
            else{
                Noon_Calories = (Calories * 0.4)- Noon_Eaten_Calories
                judge=Noon_Calories*0.38
                qq=1
            }

            while (c.moveToPosition(random)) {
                /*if(Vege!="葷"&&c.getString(4)=="葷"){
                    random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                    continue
                }*/


                total=0.0
                Carbohydrates=0.0
                protein=0.0
                fat=0.0
                i=5
                count++

                Recipe_type1=0.0
                Recipe_type2=0.0
                Recipe_type3=0.0
                Recipe_type4=0.0
                Recipe_type5=0.0
                Recipe_type6=0.0
                while(i<=1113)
                {
                    if(c.getString(i)!="0")
                    {
                        val q = MyDB2.rawQuery("select * from "+DB_TABLE2+" where Name ='"+c.getColumnName(i)+"'", null )

                        if (q.count == 0) {
                            //Toast.makeText(this, "q沒有資料", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            q.moveToFirst()
                            total=(q.getString(3).toDouble()*c.getString(i).toDouble())+total
                            Carbohydrates=(q.getString(4).toDouble()*c.getString(i).toDouble())+Carbohydrates
                            protein=(q.getString(5).toDouble()*c.getString(i).toDouble())+protein
                            fat=(q.getString(6).toDouble()*c.getString(i).toDouble())+fat

                            if(q.getString(7)!="0"){
                                Recipe_type1=Recipe_type1+(q.getString(7).toDouble())*c.getString(i).toDouble()
                                //Recipe_type=q.getColumnName(7)
                            }
                            else if(q.getString(8)!="0"){
                                Recipe_type2=Recipe_type2+(q.getString(8).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(9)!="0"){
                                Recipe_type3=Recipe_type3+(q.getString(9).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(10)!="0"){
                                Recipe_type4=Recipe_type4+(q.getString(10).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(11)!="0"){
                                Recipe_type5=Recipe_type5+(q.getString(11).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(12)!="0"){
                                Recipe_type6=Recipe_type6+(q.getString(12).toDouble())*c.getString(i).toDouble()
                            }
                            q.close()
                        }
                    }
                    i++
                }
                if(total<=Noon_Calories){

                    All_calories = All_calories + total
                    All_Recipe_type1 = All_Recipe_type1 + Recipe_type1
                    All_Recipe_type2 = All_Recipe_type2 + Recipe_type2
                    All_Recipe_type3 = All_Recipe_type3 + Recipe_type3
                    All_Recipe_type4 = All_Recipe_type4 + Recipe_type4
                    All_Recipe_type5 = All_Recipe_type5 + Recipe_type5
                    All_Recipe_type6 = All_Recipe_type6 + Recipe_type6
                    if(All_Recipe_type1<Login_Recipe_type1.toDouble()&&All_Recipe_type2<Login_Recipe_type2.toDouble()&&
                        All_Recipe_type3<Login_Recipe_type3.toDouble()&&All_Recipe_type6<Login_Recipe_type6.toDouble()){

                        arrayList_main2.add(recommand_model(c.getString(0),c.getString(1),c.getString(2),c.getString(4),
                            Decimal_point(total),Decimal_point(Carbohydrates),Decimal_point(protein),Decimal_point(fat)
                            ,Decimal_point(Recipe_type1),Decimal_point(Recipe_type2),Decimal_point(Recipe_type3),
                            Decimal_point(Recipe_type4),Decimal_point(Recipe_type5),Decimal_point(Recipe_type6),
                            c.getString(5),getDate(),"午餐"))
                        //Decimal_point 取小數點後兩位
                        Noon_Calories = Noon_Calories - total


                        if(Noon_fixed=="NULL"){
                            LoginSetting3.edit()
                                .putString("Noon_fixed"+(count-1), random.toString())  //在第65列
                                .commit()
                        }

                        if(qq==0 && LoginSetting3.getString("Noon_Count_fixed","")==count.toString()){
                            break
                        }
                    }
                    else{
                        All_calories = All_calories - total
                        All_Recipe_type1 = All_Recipe_type1 - Recipe_type1
                        All_Recipe_type2 = All_Recipe_type2 - Recipe_type2
                        All_Recipe_type3 = All_Recipe_type3 - Recipe_type3
                        All_Recipe_type4 = All_Recipe_type4 - Recipe_type4
                        All_Recipe_type5 = All_Recipe_type5 - Recipe_type5
                        All_Recipe_type6 = All_Recipe_type6 - Recipe_type6

                        count=count-1
                        if(Noon_Calories<=200){
                            break
                        }
                        else if(Noon_Calories < judge){
                            break
                        }

                    }

                }
                else{
                    count=count-1
                    if(Noon_Calories<=200){
                        break
                    }
                    else if(Noon_Calories < judge){
                        break
                    }
                }
                Noon_fixed = LoginSetting3.getString("Noon_fixed"+(count),"NULL")
                if(Noon_fixed=="NULL"){
                    random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                }
                else{
                    random = Noon_fixed.toString().toInt()
                }
                //random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值

            }
            if(count==0){//若Count=0顯示的東西
                arrayList_no2.add(no_model("No","你已經吃超量了!"))
                val myAdapter_main2 = no_adapter(arrayList_no2,this)
                rec_recyclerView2.layoutManager= LinearLayoutManager(this)
                rec_recyclerView2.adapter = myAdapter_main2

                LoginSetting3.edit()
                    .putString("Noon_fuck_you", "fuck")
                    .commit()
            }
            else{
                val myAdapter_main2 = recommand_adapter(arrayList_main2,this)
                rec_recyclerView2.layoutManager= LinearLayoutManager(this)
                rec_recyclerView2.adapter = myAdapter_main2
            }

            if(qq==1){
                LoginSetting3.edit()
                    .putString("Noon_Count_fixed", count.toString())
                    .commit()
            }

            Noon_count_text.text = "有" + count.toString() + "筆"




            time_text3.text="晚餐"

            //算卡路里 59行
            var Dinner_Calories = (Calories * 0.3)
            judge = Dinner_Calories * 0.38

            qq = 0

            c.moveToFirst()
            count=0
            var Dinner_fixed = LoginSetting3.getString("Dinner_fixed0","NULL")
            if(Dinner_fixed!="NULL"){
                random = Dinner_fixed.toString().toInt()
            }
            else{
                Dinner_Calories = (Calories * 0.3)- Dinner_Eaten_Calories
                judge = Dinner_Calories * 0.38
                qq=1
            }

            while (c.moveToPosition(random)) {
                /*if(Vege!="葷"&&c.getString(4)=="葷"){
                    random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                    continue
                }*/

                total=0.0
                Carbohydrates=0.0
                protein=0.0
                fat=0.0
                i=5
                count++

                Recipe_type1=0.0
                Recipe_type2=0.0
                Recipe_type3=0.0
                Recipe_type4=0.0
                Recipe_type5=0.0
                Recipe_type6=0.0
                while(i<=1113)
                {
                    if(c.getString(i)!="0")
                    {
                        val q = MyDB2.rawQuery("select * from "+DB_TABLE2+" where Name ='"+c.getColumnName(i)+"'", null )

                        if (q.count == 0) {
                            //Toast.makeText(this, "q沒有資料", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            q.moveToFirst()
                            total=(q.getString(3).toDouble()*c.getString(i).toDouble())+total
                            Carbohydrates=(q.getString(4).toDouble()*c.getString(i).toDouble())+Carbohydrates
                            protein=(q.getString(5).toDouble()*c.getString(i).toDouble())+protein
                            fat=(q.getString(6).toDouble()*c.getString(i).toDouble())+fat

                            if(q.getString(7)!="0"){
                                Recipe_type1=Recipe_type1+(q.getString(7).toDouble())*c.getString(i).toDouble()
                                //Recipe_type=q.getColumnName(7)
                            }
                            else if(q.getString(8)!="0"){
                                Recipe_type2=Recipe_type2+(q.getString(8).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(9)!="0"){
                                Recipe_type3=Recipe_type3+(q.getString(9).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(10)!="0"){
                                Recipe_type4=Recipe_type4+(q.getString(10).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(11)!="0"){
                                Recipe_type5=Recipe_type5+(q.getString(11).toDouble())*c.getString(i).toDouble()
                            }
                            else if(q.getString(12)!="0"){
                                Recipe_type6=Recipe_type6+(q.getString(12).toDouble())*c.getString(i).toDouble()
                            }
                            q.close()
                        }
                    }
                    i++
                }
                if(total<=Dinner_Calories){

                    All_calories = All_calories + total
                    All_Recipe_type1 = All_Recipe_type1 + Recipe_type1
                    All_Recipe_type2 = All_Recipe_type2 + Recipe_type2
                    All_Recipe_type3 = All_Recipe_type3 + Recipe_type3
                    All_Recipe_type4 = All_Recipe_type4 + Recipe_type4
                    All_Recipe_type5 = All_Recipe_type5 + Recipe_type5
                    All_Recipe_type6 = All_Recipe_type6 + Recipe_type6
                    if(All_Recipe_type1<Login_Recipe_type1.toDouble()&&All_Recipe_type2<Login_Recipe_type2.toDouble()&&
                        All_Recipe_type3<Login_Recipe_type3.toDouble()&& All_Recipe_type6<Login_Recipe_type6.toDouble()){
                        arrayList_main3.add(recommand_model(c.getString(0),c.getString(1),c.getString(2),c.getString(4),
                            Decimal_point(total),Decimal_point(Carbohydrates),Decimal_point(protein),Decimal_point(fat)
                            ,Decimal_point(Recipe_type1),Decimal_point(Recipe_type2),Decimal_point(Recipe_type3),
                            Decimal_point(Recipe_type4),Decimal_point(Recipe_type5),Decimal_point(Recipe_type6),
                            c.getString(5),getDate(),"晚餐"))
                        //Decimal_point 取小數點後兩位
                        Dinner_Calories = Dinner_Calories - total



                        if(Dinner_fixed=="NULL"){
                            LoginSetting3.edit()
                                .putString("Dinner_fixed"+(count-1), random.toString())  //在第65列
                                .commit()
                        }

                        if(qq==0 && LoginSetting3.getString("Dinner_Count_fixed","")==count.toString()){
                            break
                        }
                    }
                    else{
                        All_calories = All_calories - total
                        All_Recipe_type1 = All_Recipe_type1 - Recipe_type1
                        All_Recipe_type2 = All_Recipe_type2 - Recipe_type2
                        All_Recipe_type3 = All_Recipe_type3 - Recipe_type3
                        All_Recipe_type4 = All_Recipe_type4 - Recipe_type4
                        All_Recipe_type5 = All_Recipe_type5 - Recipe_type5
                        All_Recipe_type6 = All_Recipe_type6 - Recipe_type6

                        count=count-1
                        if(Dinner_Calories<=200){
                            break
                        }
                        else if(Dinner_Calories < judge){
                            break
                        }
                    }


                }
                else{
                    count=count-1
                    if(Dinner_Calories<=200){
                        break
                    }
                    else if(Dinner_Calories < judge){
                        break
                    }
                }

                Dinner_fixed = LoginSetting3.getString("Dinner_fixed"+(count),"NULL")
                if(Dinner_fixed=="NULL"){
                    random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
                }
                else{
                    random = Dinner_fixed.toString().toInt()
                }
                //random=(Math.random()*1833).toInt()+1 //當要取範圍0~n的亂數時，先乘上n，用toInt()取得四捨五入的值
            }
            if(count==0){//若Count=0顯示的東西
                arrayList_no3.add(no_model("No","你已經吃超量了!"))
                val myAdapter_main3 = no_adapter(arrayList_no3,this)
                rec_recyclerView3.layoutManager= LinearLayoutManager(this)
                rec_recyclerView3.adapter = myAdapter_main3

                LoginSetting3.edit()
                    .putString("Dinner_fuck_you", "fuck")
                    .commit()
            }
            else{
                val myAdapter_main3 = recommand_adapter(arrayList_main3,this)
                rec_recyclerView3.layoutManager= LinearLayoutManager(this)
                rec_recyclerView3.adapter = myAdapter_main3
            }

            if(qq==1){
                LoginSetting3.edit()
                    .putString("Dinner_Count_fixed", count.toString())
                    .commit()
            }

            Dinner_count_text.text = "有" + count.toString() + "筆"


            REC_Calories_text.text = "熱量 : " + Decimal_point(All_calories).toString()      //熱量
            REC_Recipe_type1.text = "全榖雜糧類 : " + Decimal_point(All_Recipe_type1).toString()   //全榖雜糧類
            REC_Recipe_type2.text = "豆魚蛋肉類 : " + Decimal_point(All_Recipe_type2).toString()   //蛋豆魚肉類
            REC_Recipe_type3.text = "蔬菜類 : " + Decimal_point(All_Recipe_type3).toString()   //蔬菜類
            REC_Recipe_type4.text = "水果類 : " + Decimal_point(All_Recipe_type4).toString()   //水果類
            REC_Recipe_type5.text = "乳品類 : " + Decimal_point(All_Recipe_type5).toString()   //乳品類
            REC_Recipe_type6.text = "油脂與堅果類 : " + Decimal_point(All_Recipe_type6).toString()   //油脂與堅果類
        }

    }

    fun getTime_detail(): String{

        var calendar = Calendar.getInstance()
        var created = calendar.get(Calendar.HOUR_OF_DAY).toString() + "點" + calendar.get(Calendar.MINUTE).toString() + "分" + calendar.get(
            Calendar.SECOND).toString() + "秒"
        return created
    }
    fun getTime(): String{

        var calendar = Calendar.getInstance()
        var created = calendar.get(Calendar.HOUR_OF_DAY).toString() //22.toString()
        return created
    }

    fun getDate(): String{

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR).toString()
        var month = ((calendar.get(Calendar.MONTH).toString().toInt())+1).toString()
        var date = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if(month.toInt()<10){
            month = "0"+ month //如國值小於10,前面就補0
        }
        if(date.toInt()<10){
            date = "0" + date //如國值小於10,前面就補0
        }
        var created = year+month+date
        return created
    }
    fun getDate_detail(): String{

        var calendar = Calendar.getInstance()
        var created = calendar.get(Calendar.YEAR).toString() + "年" +
                ((calendar.get(Calendar.MONTH).toString().toInt())+1).toString()+ "月" +
                calendar.get(Calendar.DAY_OF_MONTH).toString() + "日"
        return created
    }
    fun Decimal_point(score: Double): String{

        var created = Math.round(score * 100.0) / 100.0
        return created.toString()

    }
    private val mListener_refresh = View.OnClickListener {

        val LoginSetting2 = getSharedPreferences("Have_eaten", 0)
        LoginSetting2.edit()
            .clear()
            .commit()

        val LoginSetting3 = getSharedPreferences("Fixed", 0)
        LoginSetting3.edit()
            .clear()
            .commit()

        val intent = Intent()
        intent.setClass(this, Recommand_0::class.java)
        startActivity(intent)

        val dialog = ProgressDialog.show(this@Recommand,"讀取中", "請等待...", true)
        Thread(Runnable {
            try {
                Thread.sleep(20000)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dialog!!.dismiss()
            }
        }).start()
    }
    private val mListener_clean = View.OnClickListener {
        val LoginSetting2 = getSharedPreferences("Have_eaten", 0)
        LoginSetting2.edit()
            .clear()
            .commit()

        val LoginSetting3 = getSharedPreferences("Fixed", 0)
        LoginSetting3.edit()
            .clear()
            .commit()

    }
    private val mListener_back = View.OnClickListener {
        val intent = Intent()
        intent.setClass(this, Record::class.java)
        startActivity(intent)

    }
}


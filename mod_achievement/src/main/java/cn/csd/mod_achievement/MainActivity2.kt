package cn.csd.mod_achievement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.achievement_activity_main)
        val button : Button = findViewById(R.id.button1)
        button.setOnClickListener{
            val fragment = AchievementMain()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.myFragment, fragment)
            transaction.commit()
        }
    }

}
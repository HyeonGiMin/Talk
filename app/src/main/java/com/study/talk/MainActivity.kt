package com.study.talk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.study.talk.Fragment.PeopleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


       supportFragmentManager.beginTransaction().replace(R.id.mainActivity_framelayout,PeopleFragment()).commit()


    }
}

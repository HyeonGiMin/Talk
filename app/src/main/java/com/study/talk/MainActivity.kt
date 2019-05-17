package com.study.talk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.study.talk.Fragment.ChatFregment
import com.study.talk.Fragment.PeopleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mOnNavigationSelectListener=BottomNavigationView.OnNavigationItemSelectedListener {
                item->

            when(item.itemId) {
                R.id.action_people -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainActivity_framelayout, PeopleFragment()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.mainActivity_framelayout, ChatFregment()).commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

        mainActivity_bottomNavigationview.setOnNavigationItemSelectedListener(mOnNavigationSelectListener)





    }
}

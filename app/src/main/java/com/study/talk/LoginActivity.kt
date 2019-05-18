package com.study.talk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth:FirebaseAuth=FirebaseAuth.getInstance()
    lateinit var authStateListener:FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        auth.signOut()
        var remoteConfig=FirebaseRemoteConfig.getInstance()
        var splashdown = remoteConfig.getString("loading_background")



        window.statusBarColor=Color.parseColor(splashdown)

        var login=loginActivity_button_login
        var signup=loginActivity_button_signup

        login.setBackgroundColor(Color.parseColor(splashdown))
        signup.setBackgroundColor(Color.parseColor(splashdown))

        login.setOnClickListener({
            loginEvent()
        })

        signup.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        //로그인 인터페이스 리스너
        authStateListener=FirebaseAuth.AuthStateListener {
            var user:FirebaseUser?=auth?.currentUser
            if(user!=null){
                //로그인
                var intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                //로그아웃
            }
        }



    }
    fun loginEvent(){
        auth.signInWithEmailAndPassword(loginActivity_edittext_id.text.toString(),loginActivity_edittext_password.text.toString())
            .addOnCompleteListener {
                    task ->
                if(!task.isSuccessful){
                    //로그인 실패한 부분
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

}

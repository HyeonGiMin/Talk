package com.study.talk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    var remoteConfig = FirebaseRemoteConfig.getInstance()
    lateinit var linearLayout: LinearLayout





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        linearLayout=Splash_linearlayout

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.default_config)

        remoteConfig.fetch(0)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Fetch Succeeded",
                        Toast.LENGTH_SHORT).show()

                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    remoteConfig?.activateFetched()
                } else {
                    Toast.makeText(this, "Fetch Failed",
                        Toast.LENGTH_SHORT).show()
                }
                displayMessage()
            }




    }

    private fun displayMessage() {
        var splashdown = remoteConfig.getString(getString(R.string.rc_color))
        var caps = remoteConfig.getBoolean("welcome_message_caps")
        var message = remoteConfig.getString("welcome_message")

        linearLayout.setBackgroundColor(Color.parseColor(splashdown))

        if(caps){
            var builder =AlertDialog.Builder(this)
            builder.setMessage(message).setPositiveButton("확인",DialogInterface.OnClickListener { dialog, which ->
                finish()
            })
            builder.create().show()
        }else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}

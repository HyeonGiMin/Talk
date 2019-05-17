package com.study.talk

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.study.talk.model.UserModel
import kotlinx.android.synthetic.main.activity_signup.*



class SignupActivity : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    lateinit var imageUri:Uri
    lateinit var profileImageRef :StorageReference
    var userModel=UserModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupActivity_imageview_profile.setOnClickListener {
            var intent=Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent,PICK_FROM_ALBUM)
        }


        var remoteConfig = FirebaseRemoteConfig.getInstance()
        var splashdown = remoteConfig.getString(getString(R.string.rc_color))


        auth=FirebaseAuth.getInstance()

        SignupActivity_button_signup.setBackgroundColor(Color.parseColor(splashdown))


        SignupActivity_button_signup.setOnClickListener {
           if(signupActivity_edittext_email.text.toString()==null || SignupActivity_edittext_password.text.toString()==null|| SignupActivity_edittext_name.text.toString()==null||imageUri==null){
               return@setOnClickListener
            }
            var email :String=signupActivity_edittext_email.text.toString()
            var password:String=SignupActivity_edittext_password.text.toString()


            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this@SignupActivity,OnCompleteListener<AuthResult>(){
                    task ->

                    var uid=task.result!!.user.uid
                    FirebaseStorage.getInstance().reference.child("userImages").child(uid).putFile(imageUri!!).addOnCompleteListener { task ->
                        val storageRef = FirebaseStorage.getInstance().reference.child("userImages").child(uid)
                        var urlTask=storageRef.putFile(imageUri!!).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation storageRef.downloadUrl
                         //          var imageUrl= storageRef?.downloadUrl?.toString()
                         //    var imageUrl=storageRef?.downloadUrl.result
                       //            var uriTask = storageRef.downloadUrl
                       ///             var downloadUrl = uriTask.result
                        //           var imageUrl = downloadUrl.toString()
//



                        }).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                userModel.userName=SignupActivity_edittext_name.text.toString()
                                userModel.profileImageUrl= downloadUri.toString()
                                userModel.uid=FirebaseAuth.getInstance().currentUser!!.uid

                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(object : OnSuccessListener<Void> {
                                    override fun onSuccess(p0: Void?) {
                                        this@SignupActivity.finish()
                                    }
                                })

                            } else {
                                // Handle failures
                                // ...
                            }
                        }


                      //  return@addOnCompleteListener
                    }



                })


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PICK_FROM_ALBUM&&resultCode== RESULT_OK){
            signupActivity_imageview_profile.setImageURI(data?.data) //가운데 뷰를 바꿈
            imageUri=data!!.data //이미지 경로 원본

        }

    }

    companion object {
        val PICK_FROM_ALBUM=10
    }
}

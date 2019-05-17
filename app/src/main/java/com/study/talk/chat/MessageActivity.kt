package com.study.talk.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.study.talk.Fragment.PeopleFragment
import com.study.talk.R
import com.study.talk.model.ChatModel
import com.study.talk.model.UserModel
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.android.synthetic.main.item_message.*
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {


    lateinit var uid: String;
    lateinit var destinatonUid: String
    var chatRoomUid: String? = null;
    var simpleDataFormat:SimpleDateFormat= SimpleDateFormat("yyyy.MM.dd HH:mm")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        uid = FirebaseAuth.getInstance().currentUser!!.uid  //채팅을 요구하는 uid(단말기에 로그인된 uid
        destinatonUid = intent.getStringExtra("destinationUid")  //채팅을 당하는 아이디


        messageActivity_button.setOnClickListener {
            val chatModel = ChatModel()
            chatModel.users.put(uid, true)
            chatModel.users.put(destinatonUid, true)
            //    chatModel.uid = FirebaseAuth.getInstance().getCurrentUser()!!.getUid()
            //   chatModel.destinationUid = destinatonUid

            if (chatRoomUid == null) {
                messageActivity_button.isEnabled = false
                FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(chatModel)
                    .addOnSuccessListener {
                        checkChatRoom()
                    }
            } else {
                val comment: ChatModel.Companion.Comment = ChatModel.Companion.Comment()
                comment.uid = uid
                comment.message = messageActivity_editText.text.toString()
                comment.timestamp=ServerValue.TIMESTAMP

                FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments")
                    .push().setValue(comment).addOnCompleteListener {
                        messageActivity_editText.setText("")

                    }


            }
        }
        checkChatRoom()
    }

    private fun checkChatRoom() {

        FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value == null) {
                        val newRoom = ChatModel()
                        newRoom.users[uid] = true
                        newRoom.users[destinatonUid] = true
                        FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(newRoom)
                            .addOnSuccessListener { checkChatRoom() }
                        return
                    }

                    for (item in p0.getChildren()) {
                        val chatModel = item.getValue(ChatModel::class.java)
                        if (chatModel?.users!!.containsKey(destinatonUid)) {
                            chatRoomUid = item.getKey()
                            messageActivity_button.isEnabled=true
                            messageActivity_recyclerview.layoutManager=LinearLayoutManager(this@MessageActivity)
                            messageActivity_recyclerview.adapter=RecyclerViewAdpater()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

    }

    inner class RecyclerViewAdpater : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //덧글 가져오는 부분분
        var comments:MutableList<ChatModel.Companion.Comment>
        var userModel:UserModel= UserModel()

        init {

            comments=ArrayList<ChatModel.Companion.Comment>()
            FirebaseDatabase.getInstance().reference.child("users").child(destinatonUid).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                   userModel= p0.getValue(userModel::class.java)!!
                    getMessageList()
                }

            })

        }

        fun getMessageList(){
            //메시지 내용을 가져오는 함수
            FirebaseDatabase.getInstance().reference.child("chatrooms").child(chatRoomUid!!).child("comments").addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    comments.clear()

                    for(item in p0.children){
                        comments.add(item.getValue<ChatModel.Companion.Comment>(ChatModel.Companion.Comment::class.java) as ChatModel.Companion.Comment)
                    }
                    //메시지가 갱신 시켜주는 메시지
                    notifyDataSetChanged()

                    messageActivity_recyclerview.scrollToPosition(comments.size-1)
                }

            })
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
           var messageviewholder:MessageViewHolder=  (p0 as MessageViewHolder)

            //내가 보냄 메시지
            if(comments.get(p1).uid.equals(uid)){
                messageviewholder.textView_message.text = comments[p1].message
                messageviewholder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                messageviewholder.LinearLayou_destination.visibility=View.INVISIBLE
                messageviewholder.LinearLayout_main.gravity=Gravity.RIGHT
            } else{  //상대방이 보낸 메시지
                Glide.with(p0.itemView.context).load(userModel.profileImageUrl).apply(RequestOptions().circleCrop()).into(messageviewholder.imgaeView_profile)
                messageviewholder.textView_name.text=userModel.userName
                messageviewholder.LinearLayou_destination.visibility=View.VISIBLE
                messageviewholder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                messageviewholder.textView_message.text=comments[p1].message
                messageviewholder.textView_message.textSize=25f
                messageviewholder.LinearLayout_main.gravity=Gravity.LEFT
            }
            var unixTime=comments[p1].timestamp as Long
            var date =Date(unixTime)
            simpleDataFormat.timeZone= TimeZone.getTimeZone("Asia/Seoul")
            var time=simpleDataFormat.format(date)
            messageviewholder.textView_timestamp.text=time


        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view=LayoutInflater.from(p0.context).inflate(R.layout.item_message,p0,false)

            return MessageViewHolder(view)
        }
      inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView_message: TextView
            var textView_name: TextView
            var imgaeView_profile:ImageView
           var LinearLayou_destination :LinearLayout
            var LinearLayout_main:LinearLayout
          var textView_timestamp:TextView

            init {
                textView_message = view.findViewById(R.id.messageItem_textView_message)
                textView_name=view.findViewById(R.id.messageItem_textView_name)
                imgaeView_profile=view.findViewById(R.id.messageItem_imageview_profile)
                LinearLayou_destination=view.findViewById(R.id.messageItem_linearlayout_destination)
                LinearLayout_main=view.findViewById(R.id.messageItem_linearlayout_main)
                textView_timestamp=view.findViewById(R.id.messageItem_textView_timestamp)
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.fromleft,R.anim.toright)
    }
}
   /*
   fun checkChatRoom() {




       FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/" + uid!!).equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot) {
 if (p0.value == null) {
                        val newRoom = ChatModel()
                        newRoom.users[uid] = true
                        newRoom.users[destinatonUid] = true
                        FirebaseDatabase.getInstance().reference.child("chatrooms").push().setValue(newRoom)
                            .addOnSuccessListener { checkChatRoom() }
                        return
                    }

                    for (item in p0.children) {
                        val chatModel = item.getValue<ChatModel>(ChatModel::class.java)
                        if (chatModel!!.users.containsKey(destinatonUid) && chatModel!!.users.size == 2) {
                            chatRoomUid = item.key
                            messageActivity_button!!.isEnabled = true
                            messageActivity_recyclerview.layoutManager = LinearLayoutManager(this@MessageActivity)
                            messageActivity_recyclerview.adapter = RecyclerViewAdpater()
                        }
                    }

           }

           override fun onCancelled(databaseError: DatabaseError) {

           }
       })
   }
   */


package com.study.talk.Fragment


import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.study.talk.R
import com.study.talk.chat.MessageActivity
import com.study.talk.model.ChatModel
import com.study.talk.model.UserModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatFregment : Fragment() {

    var simpleDataFormat=SimpleDateFormat("yyyy.MM.dd hh:mm")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_chat_fregment,container,false)

        var recyclerView=view.findViewById(R.id.chatFreagment_recyclerview) as RecyclerView
        recyclerView.adapter=CharRecyclerViewAdpater()
        recyclerView.layoutManager=LinearLayoutManager(inflater.context)

        return view
    }

    inner class CharRecyclerViewAdpater: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var chatModels = ArrayList<ChatModel>()
        var  uid: String=FirebaseAuth.getInstance().currentUser!!.uid
        var desinationUsers=ArrayList<String>();

        init {

            FirebaseDatabase.getInstance().reference.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatModels.clear()
                    for (item in p0.children) {
                        chatModels.add(item.getValue<ChatModel>(ChatModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view=LayoutInflater.from(p0.context).inflate(R.layout.item_chat,p0,false)

            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
           return chatModels.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

            var customViewHolder =p0 as CustomViewHolder
            lateinit var destinationUid: String

            for(user in chatModels[p1].users.keys){
                if(!user.equals(uid)){
                    destinationUid=user
                    desinationUsers.add(destinationUid)
                }
            }
            FirebaseDatabase.getInstance().reference.child("users").child(destinationUid).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val userModel = p0.getValue<UserModel>(UserModel::class.java)
                    Glide.with(customViewHolder.itemView.context)
                        .load(userModel!!.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(customViewHolder.imageView)

                    customViewHolder.textView_title.text = userModel.userName
                }

            })

            //메시지를 내림 차순으로 정렬 후 마지막 메시지의 키 값을 가져오기
            var commnetMap: MutableMap<String,ChatModel.Companion.Comment> = TreeMap<String, ChatModel.Companion.Comment>(Collections.reverseOrder<Any>())
            commnetMap.putAll(chatModels[p1].comments)
            var lastMessagekey=commnetMap.keys.toTypedArray()[0]
            customViewHolder.textView_lastMessage.text = chatModels[p1].comments[lastMessagekey]!!.message

            customViewHolder.itemView.setOnClickListener {
                view ->
                var intent= Intent(view.context,MessageActivity::class.java)
                intent.putExtra("destinationUid", desinationUsers[p1])


                var activityOptions=ActivityOptions.makeCustomAnimation(view.context,R.anim.fromright,R.anim.toleft)
                startActivity(intent,activityOptions.toBundle())
            }

            //시간기록 (Time Stamp)
            simpleDataFormat.timeZone= TimeZone.getTimeZone("Asia/Seoul")

            var unixTime=chatModels[p1].comments.get(lastMessagekey)!!.timestamp as Long

            var date= Date(unixTime)
            customViewHolder.textView_timestamp.text=simpleDataFormat.format(date)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view){
            var imageView:ImageView
            var textView_title:TextView
            var textView_lastMessage:TextView
            var textView_timestamp:TextView

            init{
                imageView=view.findViewById(R.id.chatitem_imageview)
                textView_title=view.findViewById(R.id.chatitem_textview_title)
                textView_lastMessage=view.findViewById(R.id.chatitem_textview_lastmessage)
                textView_timestamp=view.findViewById(R.id.chatitem_textview_timestamp)
            }

        }
    }


}

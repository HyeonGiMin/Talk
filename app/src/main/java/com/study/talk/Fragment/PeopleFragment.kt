package com.study.talk.Fragment

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.study.talk.R
import com.study.talk.chat.MessageActivity
import com.study.talk.model.UserModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */

class PeopleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        val recyclerView = view.findViewById(R.id.peoplefragment_recyclerview) as RecyclerView
        recyclerView.adapter = PeopleFragmentRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)


        return view
    }

    internal inner class PeopleFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var userModels: MutableList<UserModel>

        init {
            userModels = ArrayList()
            FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object : ValueEventListener {
               override fun onDataChange(dataSnapshot: DataSnapshot) {
                     userModels.clear()
                   for (snapshot in dataSnapshot.children) {
                     userModels.add(snapshot.getValue(UserModel::class.java)!!)
                     }
                      notifyDataSetChanged()
//
               }

              override fun onCancelled(databaseError: DatabaseError) {

               }
            })


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)


            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


              Glide.with(holder.itemView.context)
               .load(userModels[position].profileImageUrl)
                .apply(RequestOptions().circleCrop())
                .into((holder as CustomViewHolder).imageView)
             holder.textView.text = userModels[position].userName

            holder.itemView.setOnClickListener {
                var intent=Intent(it.context,MessageActivity::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    var ActivityOptions =
                        ActivityOptions.makeCustomAnimation(view!!.context, R.anim.fromright, R.anim.toright)
                    startActivity(intent, ActivityOptions.toBundle())
                }
            }

        }

        override fun getItemCount(): Int {
            return userModels.size
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView
            var textView: TextView

            init {
                imageView = view.findViewById(R.id.frienditem_imageview) as ImageView
                textView = view.findViewById(R.id.frienditem_textview) as TextView
            }
        }
    }

}

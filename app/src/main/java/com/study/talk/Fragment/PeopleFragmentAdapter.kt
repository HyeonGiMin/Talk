package com.study.talk.Fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.study.talk.R
import com.study.talk.model.UserModel
import java.util.ArrayList



class PeopleFragmentRecyclerViewAdapter : RecyclerView.Adapter<PeopleFragmentRecyclerViewAdapter.CustomViewHolder>() {


    override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    lateinit var userModels: MutableList<UserModel>
    init {

        FirebaseDatabase.getInstance().reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onDataChange(p0: DataSnapshot) {

                    userModels.clear()  //누적되는 데이터를 없애준다
                    for (snapshot in p0.children) {
                        userModels.add(snapshot.getValue(UserModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
            }
            )

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        var view = LayoutInflater.from(p0.context).inflate(R.layout.item_friend, p0, false)

        return CustomViewHolder(view)
    }


    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return userModels.size
    }



    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.frienditem_imageview)
        var textView: TextView = view.findViewById(R.id.frienditem_textview)

    }
}

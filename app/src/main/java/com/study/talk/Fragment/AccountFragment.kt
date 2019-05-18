package com.study.talk.Fragment


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.study.talk.R
import kotlinx.android.synthetic.main.dialog_comment.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.util.HashMap


class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val button = view.findViewById(R.id.accountFragment_button_comment) as Button

        button.setOnClickListener { view -> showDialog(view.context) }

        return view
    }

    fun showDialog(context:Context){
        var builder=AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_comment, null)
        val editText = view.findViewById(R.id.commentDialog_edittext) as EditText
        builder.setView(view).setPositiveButton("확인") { dialogInterface, i ->
            var stringObjectMap = HashMap<String, Any>()
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            stringObjectMap["comment"] = editText.text.toString()
            FirebaseDatabase.getInstance().reference.child("users").child(uid).updateChildren(stringObjectMap)
        }.setNegativeButton("취소") { dialogInterface, i -> }

        builder.show()
    }
}



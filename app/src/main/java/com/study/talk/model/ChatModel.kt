package com.study.talk.model

class ChatModel{

    var users: MutableMap<String, Boolean> = HashMap() //채팅방의 유저들
    var comments: MutableMap<String, Comment> = HashMap()//채팅방의 대화내용

  //  var uid:String=""
  //  var destinationUid=""

  companion object {
      class Comment {

          var uid: String? = null
          var message: String? = null
          var timestamp:Any? =null
      }
  }

}
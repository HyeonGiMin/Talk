package com.study.talk.model



class NotificationModel{
    lateinit var to :String
     var notification=Notification()
     var data=Data()

    inner class Notification{
        var title:String=""
        var data:String=" "
    }

    inner class Data{
        var title:String=""
        var data:String=" "
    }
}
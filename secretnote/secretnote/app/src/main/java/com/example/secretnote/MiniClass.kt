package com.example.secretnote

import android.graphics.Bitmap

class MessageClassForGroup(var author:String, var message:String, var time:String, var my_message:Boolean, var readed:Boolean, var idmsg:Long)
class MessageClass( var message:String, var time:String, var my_message:Boolean, var readed:Boolean, var idmsg:Long)
class ChatClass(var login:String, var last_name:String, var first_name:String, var middle_name:String, var dont_read_msg: Int, var last_id_msg: Long, var photo_profile: Bitmap, var group_chat: Boolean)
class AttendeesChat( var login: String, var fio:String)
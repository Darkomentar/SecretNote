package com.example.secretnote

import CryptoTransport
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.io.IOException


import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import com.example.seretnote.SaveReadFile
import com.google.protobuf.ByteString
import java.io.OutputStream
import java.nio.ByteBuffer


class VM_REG : ViewModel() {
    var ip = MutableLiveData<String>("94.41.19.40")
    var port = MutableLiveData<String>("50030")


    var lastidmsg = MutableLiveData<Long>()
    var lastidmsqINapp = MutableLiveData<Int>()
    var email = MutableLiveData<String>()
    var surname = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var middle_name = MutableLiveData<String>()
    var login = MutableLiveData<String>()

    var recipientlogin = MutableLiveData<String>()
    var recipientPublicKey = MutableLiveData<String>()
    var myPrivateKey = MutableLiveData<String>()


    var password = MutableLiveData<String>()
    var code = MutableLiveData<Int>()
    var threadlocker = MutableLiveData<Boolean>()
    var updateScroll = MutableLiveData<Boolean>()
    var publicKey = MutableLiveData<String>("000000")
    var privateKey = MutableLiveData<String>("000000")

    var chatList = MutableLiveData<MutableList<ChatClass>>(mutableListOf())
    var lastidmsgFORchats = MutableLiveData<Long>(0)
    var lastidmsgFORgroupchats = MutableLiveData<Long>(0)

    var handlerMA  = MutableLiveData< Handler >()
    var messageList = MutableLiveData<MutableList<MessageClass>>(mutableListOf())
    var messageListGroup = MutableLiveData<MutableList<MessageClassForGroup>>(mutableListOf())
    var loginList = MutableLiveData<MutableList<String>>(mutableListOf())

    var publicKeyGroup = MutableLiveData<String>("000000")
    var privateKeyGroup = MutableLiveData<String>("000000")
    var serchadduserGroupChat = MutableLiveData<String>()
    var attendeesGroupChat = MutableLiveData<MutableList<AttendeesChat>>(mutableListOf())

     fun updateattendeesGroupChat(res:   List<GetUsersChatResponse>){
         attendeesGroupChat = MutableLiveData<MutableList<AttendeesChat>>(mutableListOf())
         for (unit in res) {
                attendeesGroupChat.value!!.add(AttendeesChat(unit.uLogin,unit.surname + " "+ unit.name + " "+ unit.middleName))
            }
    }


    suspend fun serchloginforgroupchat(res:   List<GetLoginsGroupChatStreamResponse>){
        var loginListHold:MutableList<String> = mutableListOf()

        withContext(Dispatchers.Main){
            for (unit in res) {
                loginListHold.add(unit.login)
            }
        }
        loginList.postValue(loginListHold.toMutableList())

        if(VM_REGg.loginList.value!!.size > 0)
        {
            Log.d("EditTextLogin", VM_REGg.loginList.value!!.get(0).toString())
        }

    }




    suspend fun serchlogin(res:   List<GetLoginsStreamResponse>){
        var loginListHold:MutableList<String> = mutableListOf()

        withContext(Dispatchers.Main){
            for (unit in res) {
                loginListHold.add(unit.login)
            }
        }
        loginList.postValue(loginListHold.toMutableList())

        if(VM_REGg.loginList.value!!.size > 0)
        {
            Log.d("EditTextLogin", VM_REGg.loginList.value!!.get(0).toString())
        }

    }

    suspend fun updateResult(res:   List<GetMsgStreamResponse>){

        val crypt = CryptoTransport()
        var messageListHold:MutableList<MessageClass> = VM_REGg.messageList.value!!.toMutableList<MessageClass>()

        withContext(Dispatchers.Main){
            lastidmsg.value = 0;
            threadlocker.value = true
            updateScroll.value = true;
            var i_h = -1
            for (unit in res) {
                i_h = i_h+1;
               // privateKey
                var cryptimsg = crypt.decryptMessage(unit.geterMsg.toByteArray(), privateKey.value!! )
                messageListHold.add(MessageClass((ByteString.copyFrom(cryptimsg)).toStringUtf8(),unit.datatime,unit.mymessage, unit.readed, unit.idmsg))
            }
            lastidmsg.value= res.get(i_h).idmsg
        }
        updateScroll.postValue(false)
        messageList.postValue(messageListHold.toMutableList())

        var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        Thread{

            while (true) {
                if (threadlocker.value==true) {
                    var idmsg:Long = sendMsg_gm_obj.getlastidmes( login.value!!,
                        password.value!!,
                        recipientlogin.value!!)
                    if (idmsg > -1) {
                        val messageListHold: MutableList<MessageClass> =
                            VM_REGg.messageList.value!!.toMutableList<MessageClass>()
                        for (msg in messageListHold) {

                            if (msg.idmsg <= idmsg) {
                                msg.readed = true;
                            }
                        }

                        messageList.postValue(messageListHold.toMutableList())
                    }
                    var errorcodee = sendMsg_gm_obj.getmsgspart(
                        login.value!!,
                        password.value!!,
                        recipientlogin.value!!,
                        lastidmsg.value!!
                    )

                    Thread.sleep(80)
                }
                else
                {
                    break;
                }
            }

        }.start()
    }

    suspend  fun updateResultPart(res: List<GetMsgPartStreamResponse>) {
        val messageListHold: MutableList<MessageClass> = VM_REGg.messageList.value!!.toMutableList<MessageClass>()
        var i_h = -1
        var proverka = false
        val crypt = CryptoTransport()
        withContext(Dispatchers.Main) {
            for (unit in res) {
                i_h = i_h + 1
                var cryptimsg = crypt.decryptMessage(unit.geterMsg.toByteArray(), privateKey.value!! )
                messageListHold.add(MessageClass((ByteString.copyFrom(cryptimsg)).toStringUtf8(),unit.datatime,unit.mymessage, unit.readed, unit.idmsg))
                proverka = true
            }
            if (proverka) {
                lastidmsg.value = res.get(i_h).idmsg
            }
        }
        if (proverka==true)
        {
            updateScroll.postValue(false)
        }
        messageList.postValue(messageListHold.toMutableList())
    }

    suspend fun updateResultGroup(res:   List<GetMsgGroupStreamResponse>){
        Log.d("urgp", 0.toString())
        val crypt = CryptoTransport()
        var messageListGroupHold:MutableList<MessageClassForGroup> = VM_REGg.messageListGroup.value!!.toMutableList<MessageClassForGroup>()

        withContext(Dispatchers.Main){
            lastidmsg.value = 0;
            threadlocker.value = true
            updateScroll.value = true;
            var i_h = -1
            for (unit in res) {
                i_h = i_h+1;
                // privateKey
                Log.d("urgp", 1.toString())
                var cryptimsg = crypt.decryptMessage(unit.geterMsg.toByteArray(), privateKeyGroup.value!! )
                messageListGroupHold.add(MessageClassForGroup(unit.autor ,(ByteString.copyFrom(cryptimsg)).toStringUtf8(),unit.datatime,unit.mymessage, unit.readed, unit.idmsg))
                Log.d("urgp", 2.toString())
            }
            lastidmsg.value= res.get(i_h).idmsg
        }
        updateScroll.postValue(false);
        messageListGroup.postValue(messageListGroupHold.toMutableList())

        var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        Thread{

            while (true) {
               // updateScroll.postValue(true);
                if (threadlocker.value==true) {
                    var idmsg:Long = sendMsg_gm_obj.getlastidmesgroup( login.value!!,
                        password.value!!,
                        recipientlogin.value!!)
                    Log.d("urgp", idmsg.toString())
                    if (idmsg > -1) {

                        val messageListGroupHold: MutableList<MessageClassForGroup> =
                            VM_REGg.messageListGroup.value!!.toMutableList<MessageClassForGroup>()
                        for (msg in messageListGroupHold) {

                            if (msg.idmsg <= idmsg) {
                                msg.readed = true;
                            }
                        }

                        messageListGroup.postValue(messageListGroupHold.toMutableList())
                    }
                    var errorcodee = sendMsg_gm_obj.getmsggroupspart(
                        login.value!!,
                        password.value!!,
                        recipientlogin.value!!,
                        lastidmsg.value!!
                    )
//                    updateScroll.postValue(false);
                    Thread.sleep(80)
                }
                else
                {
                    break;
                }
            }

        }.start()
    }

    suspend  fun updateResultGroupPart(res: List<GetMsgGroupPartStreamResponse>) {
        val messageListGroupHold: MutableList<MessageClassForGroup> = VM_REGg.messageListGroup.value!!.toMutableList<MessageClassForGroup>()
        var i_h = -1
        var proverka = false
        val crypt = CryptoTransport()
        withContext(Dispatchers.Main) {
            for (unit in res) {
                i_h = i_h + 1
                Log.d("urgp", unit.toString())
                var cryptimsg = crypt.decryptMessage(unit.geterMsg.toByteArray(), privateKeyGroup.value!! )
                messageListGroupHold.add(MessageClassForGroup(unit.autor ,(ByteString.copyFrom(cryptimsg)).toStringUtf8(),unit.datatime,unit.mymessage, unit.readed, unit.idmsg))
                proverka = true
            }
            if (proverka) {
                lastidmsg.value = res.get(i_h).idmsg
            }
        }
        if(proverka==true)
        {
            updateScroll.postValue(false);
        }
        messageListGroup.postValue(messageListGroupHold.toMutableList())
    }

    suspend fun updatechatlist(res:   List<ChatListSResponse>){

        var chatListHold:MutableList<ChatClass> = mutableListOf()
        var flag = true;
        withContext(Dispatchers.Main){
            var i_h = -1
            var flagg_for_lich_chat = true
            var flagg_for_group_chat = true
            for (unit in res) {
                i_h = i_h+1;
                val byteArray: ByteArray = unit.photo.toByteArray();
                try {
                    val drawable: Bitmap;
                    drawable =byteArrayToBitmap( byteArray);
                    chatListHold.add(ChatClass(unit.login, unit.lastname,unit.firstname,unit.middlename, unit.countmsg, unit.lastidmsg, drawable, unit.groupChat))
                    if((flagg_for_lich_chat==true)&&(unit.groupChat == false))
                    {
                        lastidmsgFORchats.value= res.get(i_h).lastidmsg
                        flagg_for_lich_chat = false
                    }
                    if((flagg_for_group_chat==true)&&(unit.groupChat == true))
                    {
                        lastidmsgFORgroupchats.value= res.get(i_h).lastidmsg
                        flagg_for_group_chat = false
                    }
                } catch (e: IOException) {
                    Log.d("exxx", e.toString())
                }

            }
            Log.d("ull","8st -")
            flag = false
            // lastidmsgFORchats.value= 1;
        }
        while (flag==true)
        {
            Thread.sleep(30)
        }
        chatList.postValue(chatListHold.toMutableList())


    }


    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        val options = BitmapFactory.Options().apply {
            inMutable = true
        }
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }
}

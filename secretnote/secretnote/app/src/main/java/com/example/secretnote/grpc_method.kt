package com.example.secretnote

import CryptoTransport
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.protobuf.ByteString
import io.grpc.ManagedChannelBuilder
import io.grpc.util.AdvancedTlsX509KeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class grpc_method(host: String,
                  port: String) : AdvancedTlsX509KeyManager.Closeable {
    val responseState = mutableStateOf("")

    lateinit var grpc_Connect: grpc_method

    fun check_chat_list(): Int
    {
        return chatlistStream(VM_REGg.login.value.toString(), VM_REGg.password.value.toString())
    }
    private val channel = let {
        println("Connecting to ${host}:${port}")
        val builder = ManagedChannelBuilder.forAddress(host,port.toInt())
        builder.executor(Dispatchers.IO.asExecutor()).usePlaintext().enableRetry().build()
    }

    private val greeter = NuzhniitestGrpc.newBlockingStub(channel)
    fun superfunc(name: String)  {

        try {
            val request = NuzhniiRequest.newBuilder().setMessage(name).setNumber("3333").build()
            val response = greeter.superfunc(request)
            responseState.value = response.message
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
        }
    }
    fun emailconfimation(email: String):Int {
        try {
            var request = EmailConfirmationRequest.newBuilder().setEmail(email).build()
            val response = greeter.emailConfirmation(request)

            return response.errorcode

        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3
        }
    }

    fun Registration(surname: String, name: String, middle_name: String, password: String, login: String, email: String, confirmcode: Int, publickey: String):Int {
        try {
            var request = RegistrationRequest.newBuilder().setSurname(surname).setName(name).setMiddleName(middle_name).setPassword(password).setLogin(login).setEmail(email).setConfirmcode(confirmcode).setPublickey(publickey).build()
            val response = greeter.registration(request)
            return response.errorcode
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3
        }
    }



    fun login(login_a: String, paswword_a: String) : Int {
        try {
            var request = LoginRequest.newBuilder().setLogin(login_a).setPassword(paswword_a).build()
            val response = greeter.login(request)

            entered.value = response.entered
            email.value = response.email
            phone.value = response.phone
            info.value = response.info
            surname.value = response.surname
            name.value = response.name
            middle_name.value = response.middleName

            if (response.entered == true){
                return 0 // вошел
            }
            else
            {
                return 1 // не правильный логин или пароль
            }

        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3;
        }
    }

    fun loginconnect(login_a: String, paswword_a: String): Int  {
        try {
            var request = LoginConnectRequest.newBuilder().setLogin(login_a).setPassword(paswword_a).build()
            val response = greeter.loginConnect(request)
            return response.errorcode;
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3;
        }
    }

    fun sendmsg(login_a: String, paswword_a: String, recipient_login: String, senderMsg: ByteString, recipientMsg: ByteString, typemsg: String): Int
    {
        try {
            val crypt = CryptoTransport()
            var cryptoSenderMsg = crypt.encryptMessage((senderMsg).toByteArray(), VM_REGg.publicKey.value!!)
            var cryptoRecipientMsg =  crypt.encryptMessage((senderMsg).toByteArray(), VM_REGg.recipientPublicKey.value!!)
            var request = SendMsgRequest.newBuilder().setLogin(login_a).setPassword(paswword_a).setRecipientLogin(recipient_login).setSenderMsg(ByteString.copyFrom(cryptoSenderMsg)).setRecipientMsg(ByteString.copyFrom(cryptoRecipientMsg)).setTypemsg(typemsg).build()
            val response = greeter.sendMsg(request)
            return response.errorcode;


        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3;
        }
    }

    fun getlogin(recipient_login: String): Int {
        var errorcodee = 0
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {
            var request =
                GetLoginsRequest.newBuilder().setLogin(recipient_login).build()

            try {
                val response = greeter.getLogins(request).asSequence().toList()
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.get(0).errorcode
                if (errorcodee == 0) {
                    VM_REGg.serchlogin(response)
                }
            } catch (e: Exception) {
                errorcodee = -3
            }
        }
        return errorcodee
    }


    fun getmsgs(login_a: String, paswword_a: String, recipient_login: String): Int {
        var errorcodee = 3;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {


            var request =
                GetMsgStreamRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                    .setRecipientLogin(recipient_login).build()
                while (errorcodee == 3) {
                    try {
                        val response = greeter.getMsgS(request).asSequence().toList()
                        // val features = blockingStub.listFeatures(request).asSequence().toList()
                        errorcodee = response.get(0).errorcode
                        if (errorcodee == 0) {
                            VM_REGg.updateResult(response)
                        }
                    } catch (e: Exception) {
                        errorcodee = 3
                    }
                    Thread.sleep(30)
                }
        }
        return errorcodee
    }

    fun getmsgspart(login_a: String, paswword_a: String, recipient_login: String, lastidmsg: Long): Int {
        var errorcodee = 0;
        var flag = true;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {

            var request =
                GetMsgPartStreamRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                    .setRecipientLogin(recipient_login).setIdmsgLast(lastidmsg).build()
            try {
                val response = greeter.getMsgSPart(request).asSequence().toList()
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.get(0).errorcode
                VM_REGg.updateResultPart(response)

            } catch (e: Exception) {
                errorcodee = 3
            }
            flag = false
        }
        while(flag) Thread.sleep(30);
        return errorcodee
    }

    fun getlastidmes(login_a: String, paswword_a: String, recipient_login: String): Long {
        var errorcodee:Long = 0;
        var request =
            GetLastReadedRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                .setRecipientLogin(recipient_login).build()
        try {
            val response = greeter.getLastReaded(request)
            // val features = blockingStub.listFeatures(request).asSequence().toList()
            errorcodee = response.idmsgLastReaded.toLong()

        } catch (e: Exception) {
            errorcodee = -3
        }
        return errorcodee
    }
    fun chatlistStream(login_a: String, paswword_a: String): Int {
        var errorcodee = 0;
        var flag = true;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {
            var request =
                ChatListSRequest.newBuilder().setLogin(login_a).setPassword(paswword_a).build()
            try {
                val response = greeter.chatListS(request).asSequence().toList()
                if(response.count()>0)
                {
                    errorcodee = response.get(0).errorcode
                    if (errorcodee == 0)
                    {
                        VM_REGg.updatechatlist(response);
                    }
                }
                else
                {
                    errorcodee = -2
                }
            } catch (e: Exception) {
                errorcodee = -3
            }
            flag = false
        }
        while(flag) Thread.sleep(30);
        return errorcodee
    }


    fun check_new_msg(login_a: String, paswword_a: String, lastidmsg: Long, lastidmsggroup: Long): Long {
        var errorcodee:Long = 0;
        var request = CheckNewMsgRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
            .setIdlastmsg(lastidmsg).setIdlastmsggroup(lastidmsggroup).build()
        try {
            val response = greeter.checkNewMsg(request)
            return response.errorcodeOridlastmsg;
        } catch (e: Exception) {
            errorcodee = -3

        }
        return errorcodee
    }

    fun get_publick_key(login_a: String): String {
        var errorcodeOrPublickKey:String = "0";
        var request = GetPublickKeyRequest.newBuilder().setLogin(login_a).build()
        try {
            val response = greeter.getPublickKey(request)
            return response.errorcodeOrPublickKey;
        } catch (e: Exception) {
            errorcodeOrPublickKey = "-3"
        }
        return errorcodeOrPublickKey
    }
    fun create_group_chat(login: String,paswword: String,nameChat: String,loginChat: String,publicKey: String,privateKey: ByteArray): Int {
        var errorcode:Int = 0;
        val crypt = CryptoTransport()
        var cryptoSenderMsg:ByteArray = "ff".toByteArray()
        cryptoSenderMsg = crypt.encryptMessage(privateKey, VM_REGg.publicKey.value!!)
        Log.d("urgp", "Normalnovse")
        var firstmsg:ByteArray = crypt.encryptMessage("Создал чат".toByteArray(), publicKey)
        var request = CreateGroupChatRequest.newBuilder().setLogin(login).setPassword(paswword).setNameChat(nameChat).setLoginChat(loginChat).setPublicKey(publicKey).setPrivateKey(ByteString.copyFrom(cryptoSenderMsg)).setFirstMsg(ByteString.copyFrom(firstmsg)).build()
        try {
            val response = greeter.createGroupChat(request)
            return response.errorcode;
        } catch (e: Exception) {
            errorcode = 3
        }
        return errorcode
    }


    fun getmsggroups(login_a: String, paswword_a: String, recipient_login: String): Int {
        var errorcodee = 3;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {


            var request =
                GetMsgGroupStreamRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                    .setRecipientLogin(recipient_login).build()
            while (errorcodee == 3) {
                try {
                    val response = greeter.getMsgGroupS(request).asSequence().toList()
                    // val features = blockingStub.listFeatures(request).asSequence().toList()
                    errorcodee = response.get(0).errorcode
                    if (errorcodee == 0) {
                        VM_REGg.updateResultGroup(response)
                    }
                } catch (e: Exception) {
                    errorcodee = 3
                }
                Thread.sleep(30)
            }
        }
        return errorcodee
    }

    fun getmsggroupspart(login_a: String, paswword_a: String, recipient_login: String, lastidmsg: Long): Int {
        var errorcodee = 0;
        var flag = true;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {

            var request =
                GetMsgGroupPartStreamRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                    .setRecipientLogin(recipient_login).setIdmsgLast(lastidmsg).build()
            try {
                val response = greeter.getMsgGroupSPart(request).asSequence().toList()
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.get(0).errorcode
                VM_REGg.updateResultGroupPart(response)

            } catch (e: Exception) {
                errorcodee = 3
            }
            flag = false
        }
        while(flag) Thread.sleep(30);
        return errorcodee
    }

    fun getusersgroupchat(): Int {
        var errorcodee = 0;
        var flag = true;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {

            var request =
                GetUsersChatRequest.newBuilder().setLogin(VM_REGg.login.value.toString()).setPassword(VM_REGg.password.value.toString())
                    .setLoginChat(VM_REGg.recipientlogin.value.toString()).build()
            try {
                val response = greeter.getUsersChat(request).asSequence().toList()
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.get(0).errorcode
                VM_REGg.updateattendeesGroupChat(response)

            } catch (e: Exception) {
                errorcodee = 3
            }
            flag = false
        }
        while(flag) Thread.sleep(30);
        return errorcodee
    }

    fun getlastidmesgroup(login_a: String, paswword_a: String, recipient_login: String): Long {
        var errorcodee:Long = 0;
        var request =
            GetLastReadedGroupRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                .setRecipientLogin(recipient_login).build()
        try {
            val response = greeter.getLastReadedGroup(request)
            // val features = blockingStub.listFeatures(request).asSequence().toList()
            errorcodee = response.idmsgLastReaded.toLong()

        } catch (e: Exception) {
            errorcodee = -3
        }
        return errorcodee
    }


    fun setmsgchatgroup(login_a: String, paswword_a: String, recipient_login: String, Msg: ByteString, typemsg: String): Int
    {
        try {
            val crypt = CryptoTransport()
            var cryptoSenderMsg = crypt.encryptMessage(Msg.toByteArray(), VM_REGg.publicKeyGroup.value!!)
            Log.d("smcg", "cryptoSenderMsg_OK")
            var request = SetMsgGroupChatRequest.newBuilder().setLogin(login_a).setPassword(paswword_a).setRecipientLogin(recipient_login).setMsg(ByteString.copyFrom(cryptoSenderMsg)).setTypemsg(typemsg).build()
            Log.d("smcg", "cryptoSenderMsg_OK")
            val response = greeter.setMsgGroupChat(request)
            Log.d("smcg", "cryptoSenderMsg_OK")
            return response.errorcode;


        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
            return 3;
        }
    }
    fun get_pu_pr_key_group_chat(login_a: String, paswword_a: String, recipient_login: String): Int {
        var errorcodee = 0;
        var flag = true;
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {
            val crypt = CryptoTransport()
            var request =
                GetPuPrKeyGroupChatRequest.newBuilder().setLogin(login_a).setPassword(paswword_a)
                    .setLoginChat(recipient_login).build()
            try {
                val response = greeter.getPuPrKeyGroupChat(request)
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                Log.d("urgp", response.errorcode.toString())
                errorcodee = response.errorcode
                VM_REGg.publicKeyGroup.postValue(response.publickey)
                Log.d("urgp", response.publickey.toString())
//                var privateKeyGroupResp = crypt.decryptMessage(response.privatekey.toByteArray(), VM_REGg.privateKey.value!!)
                var huita  = VM_REGg.privateKey.value!!
                Log.d("urgp", "dfdd".toString())
                var huita2  =  response.privatekey.toByteArray()
                Log.d("urgp", "dfdd".toString())
                var privateKeyGroupResp = crypt.decryptMessage(response.privatekey.toByteArray(), VM_REGg.privateKey.value!!)
                Log.d("urgp", response.publickey.toString())
                VM_REGg.privateKeyGroup.postValue(ByteString.copyFrom(privateKeyGroupResp).toStringUtf8())

            }
            catch (e: Exception) {
                errorcodee = 3
            }
            flag = false
        }
        while(flag) Thread.sleep(30);
        return errorcodee
    }



    fun getloginsGroupChat(login: String): Int {
        var errorcodee = 0
        VM_REGg.viewModelScope.launch(Dispatchers.IO) {
            var request =
                GetLoginsGroupChatRequest.newBuilder().setLogin(login).setLoginChat(VM_REGg.recipientlogin.value!!).build()

            try {
                val response = greeter.getLoginsGroupChat(request).asSequence().toList()
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.get(0).errorcode
                if (errorcodee == 0) {
                    VM_REGg.serchloginforgroupchat(response)
                }
            } catch (e: Exception) {
                errorcodee = -3
            }
        }
        return errorcodee
    }

    fun addusergroupchat(u_login: String): Int {
        var errorcodee = 0

            var cr = CryptoTransport()
            var ecr_private_key = cr.encryptMessage(VM_REGg.privateKeyGroup.value!!.toByteArray(), get_publick_key(u_login))
            var request =
                AddUserGroupChatRequest.newBuilder().setLogin(VM_REGg.login.value.toString()).setPassword(VM_REGg.password.value.toString()).setULogin(u_login)
                    .setLoginChat(VM_REGg.recipientlogin.value.toString()).setPrivateKey(ByteString.copyFrom(ecr_private_key)).build()

            try {
                val response = greeter.addUserGroupChat(request)
                // val features = blockingStub.listFeatures(request).asSequence().toList()
                errorcodee = response.errorcode

            } catch (e: Exception) {
                errorcodee = -3
            }

        return errorcodee
    }

    fun quitgroupchat(chat_login: String): Int {
        var errorcodee = 0
        var request =
            QuitGroupChatRequest.newBuilder().setLogin(VM_REGg.login.value.toString()).setPassword(VM_REGg.password.value.toString()).setLoginChat(chat_login).build()

        try {
            val response = greeter.quitGroupChat(request)
            // val features = blockingStub.listFeatures(request).asSequence().toList()
            errorcodee = response.errorcode

        } catch (e: Exception) {
            errorcodee = -3
        }

        return errorcodee
    }

    fun quitchat(login: String): Int {
        var errorcodee = 0
        var request =
            QuitChatRequest.newBuilder().setLogin(VM_REGg.login.value.toString()).setPassword(VM_REGg.password.value.toString()).setLoginInterlocutor(login).build()

        try {
            val response = greeter.quitChat(request)
            // val features = blockingStub.listFeatures(request).asSequence().toList()
            errorcodee = response.errorcode

        } catch (e: Exception) {
            errorcodee = -3
        }

        return errorcodee
    }

    override fun close() {
        channel.shutdownNow()
    }




    companion object {
        public var entered = mutableStateOf(true)
        var email= mutableStateOf("true")
        var phone= mutableStateOf("true")
        var info= mutableStateOf("true")
        var surname= mutableStateOf("true")
        var name= mutableStateOf("true")
        var middle_name= mutableStateOf("true")
    }

}

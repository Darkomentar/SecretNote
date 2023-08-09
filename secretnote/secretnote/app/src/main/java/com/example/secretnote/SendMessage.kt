package com.example.secretnote

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class SendMessage : Fragment() {
    lateinit var recyclerView: RecyclerView;
    lateinit var recyclerViewAdapter: RecycleViewAdapterMessages


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }
 var un:Int = 0


    fun Fragment.setActivityTitle(title: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }
    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        val view = inflater.inflate(R.layout.fragment_send_message, container, false)

        Log.d("VMs","senmessedg claasss")
        val myButton = view.findViewById<FloatingActionButton>(R.id.floatingActionSendButton)
        Log.d("VMs","senmessedg claasss")
        var j = sendMsg_gm_obj.get_publick_key(VM_REGg.recipientlogin.value!!)
        if (j=="-1")
        {
            Toast.makeText(activity, "Нет такого пользователя", Toast.LENGTH_LONG).show();
        }
        else if (j=="-3")
        {
            Toast.makeText(activity, "Нет доступа к серверу", Toast.LENGTH_LONG).show();
        }
        else {
            VM_REGg.recipientPublicKey.value = j
            myButton.setOnClickListener {
                var text_is_textview =
                    (view.findViewById<EditText>(R.id.message_text)).text.toString()
                var mesg: ByteString = ByteString.copyFrom(text_is_textview.toByteArray())
                var errorcode = sendMsg_gm_obj.sendmsg(
                    VM_REGg.login.value!!,
                    VM_REGg.password.value!!,
                    VM_REGg.recipientlogin.value!!, mesg, mesg, "text"
                );
                if (errorcode == 0) {
                    //Toast.makeText(activity, "Сообщение отправлено", Toast.LENGTH_LONG).show();
                    while (un == 0) {
                        Thread.sleep(50)
                    }
                    recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
                    un = 0
                }
                if (errorcode == 1) Toast.makeText(
                    activity,
                    "Сообщение не отправлено",
                    Toast.LENGTH_LONG
                ).show();
                if (errorcode == 2) Toast.makeText(
                    activity,
                    "Нет такого юзера или пишешь самому себе",
                    Toast.LENGTH_LONG
                ).show();
                if (errorcode == 3) Toast.makeText(
                    activity,
                    "Сообщение не отправлено так как нет доступа в интернет",
                    Toast.LENGTH_LONG
                ).show()
                view.findViewById<EditText>(R.id.message_text).setText("")
            }
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMessages);

        recyclerViewAdapter = RecycleViewAdapterMessages(chats_k, VM_REGg.messageList.value!!.toMutableList<MessageClass>())
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)


     //   var g = 0;

        VM_REGg.messageList.observe(this) {

            recyclerViewAdapter.updateData(VM_REGg.messageList.value!!.toMutableList<MessageClass>())
            recyclerViewAdapter.notifyDataSetChanged()
            un = 1
            if(VM_REGg.updateScroll.value==false)
            {
                recyclerView.scrollToPosition(recyclerViewAdapter.itemCount-1)
                VM_REGg.updateScroll.value = true;
            }
        }
        setActivityTitle(VM_REGg.recipientlogin.value.toString())


        return view
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
    }





    override fun onPrepareOptionsMenu(menu: Menu){ // вызывается при обновлении меню
        super.onPrepareOptionsMenu(menu)
        Log.d("testMenuAppBar", "obnovaMenu")
        val item2 = menu.findItem(R.id.exiting_the_chat)
        item2.isVisible = true
        item2.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exiting_the_chat -> {
                    //тут типа шо делать при нажатии на выйти для личного чата
                    var gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                    var error = gm_obj.quitchat(VM_REGg.recipientlogin.value!!.toString())
                    if(error == -1)
                    {
                        Toast.makeText(activity, "Неверный логин/пароль", Toast.LENGTH_LONG).show();
                    }
                    if(error == -2)
                    {
                        Toast.makeText(activity, "Нет такого пользователя", Toast.LENGTH_LONG).show();
                    }
                    if(error == 0)
                    {
                        VM_REGg.threadlocker.value = false;
                        VM_REGg.lastidmsgFORchats.value = 0
                        this.findNavController().navigate(R.id.action_sendMessage2_to_nav_home)
                    }
                    Log.d("testMenuAppBar", "UserNazhalAdd")
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
    }
    override fun onDestroyView() {
        VM_REGg.threadlocker.value = false;
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDestroyView()
    }



}
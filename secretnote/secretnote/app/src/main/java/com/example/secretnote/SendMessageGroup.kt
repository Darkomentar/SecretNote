package com.example.secretnote

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.protobuf.ByteString



class SendMessageGroup : Fragment() {
    lateinit var recyclerView: RecyclerView;
    lateinit var recyclerViewAdapter: RecycleViewAdapterMessagesGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // указываем шо фрагмент имеет своё меню
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }
     var un:Int = 0



    override fun onPrepareOptionsMenu(menu: Menu){ // вызывается при обновлении меню
        super.onPrepareOptionsMenu(menu)
        Log.d("testMenuAppBar", "obnovaMenu")
        val item = menu.findItem(R.id.action_add_user_group_chat)
        item.isVisible = true
        val item1 = menu.findItem(R.id.attendees)
        item1.isVisible = true
        val item2 = menu.findItem(R.id.exiting_the_chat)
        item2.isVisible = true
        item.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.action_add_user_group_chat -> {
                    //тут типа шо делать при нажатии на добавить пользователя
                    this.findNavController().navigate(R.id.action_sendMessageGroup_to_addUserGroupChat)
                    Log.d("testMenuAppBar", "UserNazhalAdd")
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        item1.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.attendees -> {
                    //тут типа шо делать при нажатии на список пользователей
                    this.findNavController().navigate(R.id.action_sendMessageGroup_to_attendees2)
                    Log.d("testMenuAppBar", "UserNazhalAdd")
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }

        item2.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exiting_the_chat -> {
                    //тут типа шо делать при нажатии на выйти для группового чата
                    var gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                    var error = gm_obj.quitgroupchat(VM_REGg.recipientlogin.value!!.toString())
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
                        this.findNavController().navigate(R.id.action_sendMessageGroup_to_nav_home)
                    }
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
    }


    fun Fragment.setActivityTitle(title: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }

    @SuppressLint("FragmentLiveDataObserve", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.d("ffoncret", "cortttt")
//        VM_REGg.threadlocker.value = true;
        var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        val view = inflater.inflate(R.layout.fragment_send_message_group, container, false)


        val myButton = view.findViewById<FloatingActionButton>(R.id.floatingActionSendButtonGroup)

            VM_REGg.recipientPublicKey.value = VM_REGg.publicKeyGroup.value
        myButton.setOnClickListener {
                var text_is_textview =
                    (view.findViewById<EditText>(R.id.message_textGroup)).text.toString()
                var mesg: ByteString = ByteString.copyFrom(text_is_textview.toByteArray())
                var errorcode = sendMsg_gm_obj.setmsgchatgroup(
                    VM_REGg.login.value!!,
                    VM_REGg.password.value!!,
                    VM_REGg.recipientlogin.value!!, mesg, "text"
                );
                Log.d("SMG", errorcode.toString())
            Log.d("SMG",   VM_REGg.login.value!!)
            Log.d("SMG",   VM_REGg.password.value!!)
            Log.d("SMG",   VM_REGg.recipientlogin.value!!)

                if (errorcode == 0) {
                    //Toast.makeText(activity, "Сообщение отправлено", Toast.LENGTH_LONG).show();
                    while (un == 0) {
                        Thread.sleep(50)
                    }
                    recyclerView.scrollToPosition(recyclerViewAdapter.itemCount - 1)
                    un = 0
                }
                if (errorcode == -1) Toast.makeText(
                    activity,
                    "Нет такого чата",
                    Toast.LENGTH_LONG
                ).show();
                if (errorcode == -2) Toast.makeText(
                    activity,
                    "логин и пароль не верны",
                    Toast.LENGTH_LONG
                ).show();
                if (errorcode == 3) Toast.makeText(
                    activity,
                    "Сообщение не отправлено так как нет доступа в интернет",
                    Toast.LENGTH_LONG
                ).show()
                view.findViewById<EditText>(R.id.message_textGroup).setText("")

        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMessagesGroup);

        recyclerViewAdapter = RecycleViewAdapterMessagesGroup(
            chats_k,
            VM_REGg.messageListGroup.value!!.toMutableList<MessageClassForGroup>()
        )
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)


     //   var g = 0;

        VM_REGg.messageListGroup.observe(this) {

            recyclerViewAdapter.updateData(VM_REGg.messageListGroup.value!!.toMutableList<MessageClassForGroup>())
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

    override fun onDestroyView() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onDestroyView()
    }




}
package com.example.secretnote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Attendees : Fragment() {

    lateinit var recyclerView: RecyclerView;
    lateinit var recyclerViewAdapter: RecyclerViewAdapterAttendees


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment  recyclerViewAttendees
        (activity as? AppCompatActivity)?.supportActionBar?.title = "ATTENDEES"
        val view = inflater.inflate(R.layout.fragment_attendees, container, false)

        var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        sendMsg_gm_obj.getusersgroupchat();

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAttendees);
        var attendeesGroupChat = MutableLiveData<MutableList<AttendeesChat>>(mutableListOf())

        recyclerViewAdapter = RecyclerViewAdapterAttendees(
            chats_k,
            VM_REGg.attendeesGroupChat.value!!.toMutableList<AttendeesChat>()
        )

        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)



        return view
    }


}
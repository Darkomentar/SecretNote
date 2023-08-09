package com.example.secretnote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddUserGroupChat.newInstance] factory method to
 * create an instance of this fragment.
 */

class AddUserGroupChat : Fragment() {
    lateinit var recyclerView2: RecyclerView;
    lateinit var recyclerViewAdapter2: RecycleViewAdapterSearchAddUserGroup;
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_user_group_chat, container, false)
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.title = "ADD USER IN GROUP CHAT"


        recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerViewLogins);
        recyclerViewAdapter2 = RecycleViewAdapterSearchAddUserGroup(chats_k, VM_REGg.loginList.value!!.toMutableList<String>())
        recyclerView2.adapter = recyclerViewAdapter2
        recyclerView2.layoutManager = LinearLayoutManager(view.context)


        VM_REGg.loginList.observe(this.viewLifecycleOwner) {
            //Log.d("EditTextLogin", "OBESRVERAAAAAAAAAAAAAAAAAAAAAAAAAA")
            recyclerViewAdapter2.updateData(VM_REGg.loginList.value!!.toMutableList<String>())
            recyclerViewAdapter2.notifyDataSetChanged()

        }




        view.findViewById<EditText>(R.id.EditTextSerchLogins).addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) {
                var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                sendMsg_gm_obj.getloginsGroupChat(s.toString())
                VM_REGg.serchadduserGroupChat.value = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddUserGroupChat.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddUserGroupChat().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
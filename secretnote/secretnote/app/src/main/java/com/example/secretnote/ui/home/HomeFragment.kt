package com.example.secretnote.ui.home

import CryptoTransport
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secretnote.ChatClass
import com.example.secretnote.R
import com.example.secretnote.RecyclerViewAdapter
import com.example.secretnote.VM_REG
import com.example.secretnote.VM_REGg
import com.example.secretnote.chats_k
import com.example.secretnote.databinding.FragmentHomeBinding
import com.example.secretnote.grpc_method
import com.google.protobuf.ByteString
import okio.utf8Size


class HomeFragment : Fragment() {
    lateinit var recyclerView1: RecyclerView;
    lateinit var recyclerViewAdapter1: RecyclerViewAdapter

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onResume() {
        VM_REGg.threadlocker.value = false;
        super.onResume()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // With blank your fragment BackPressed will be disabled.
        }
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView1 = view.findViewById<RecyclerView>(R.id.recyclerViewChats);
        recyclerViewAdapter1 = RecyclerViewAdapter(chats_k, VM_REGg.chatList.value!!.toMutableList<ChatClass>())
        recyclerView1.adapter = recyclerViewAdapter1
        recyclerView1.layoutManager = LinearLayoutManager(view.context)


        //   var g = 0;

        VM_REGg.chatList.observe(this.viewLifecycleOwner) {
            recyclerViewAdapter1.updateData(VM_REGg.chatList.value!!.toMutableList<ChatClass>())
            recyclerViewAdapter1.notifyDataSetChanged()
        }



        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

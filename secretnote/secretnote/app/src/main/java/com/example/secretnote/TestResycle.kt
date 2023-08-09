//package com.example.secretnote
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//class TestResycle : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
//   // private var itemList = mutableListOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
//
//    @SuppressLint("NotifyDataSetChanged")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_test_resycle)
//
////            lifecycleScope.launch {
////
////                itemList += GrpcClientService.grpc.chatlist(
////                    VM_REGg.login.value.toString(),
////                    VM_REGg.password.value.toString()
////                ).toString()
////                Log.d("fdd", itemList.toString())
////            }
////
////        recyclerView = findViewById(R.id.recycler_view)
////        recyclerViewAdapter = RecyclerViewAdapter(VM_REGg.chatList.value!!.toMutableList<ChatClass>())
////        VM_REGg.chatList.observe(this) {
//////            recyclerViewAdapter.notifyDataSetChanged()
//////            recyclerViewAdapter.notifyItemInserted(VM_REGg.chatList.value!!.toMutableList<String>());
////            recyclerViewAdapter.updateData(VM_REGg.chatList.value!!.toMutableList<ChatClass>())
////           // recyclerViewAdapter.submitList(VM_REGg.chatList.value!!.toMutableList<String>());
////            recyclerViewAdapter.notifyDataSetChanged();
//////        }
////        recyclerView.adapter = recyclerViewAdapter
////        recyclerView.layoutManager = LinearLayoutManager(this)
//
//    }
//}
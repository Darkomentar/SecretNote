package com.example.secretnote.ui.gallery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secretnote.R
import com.example.secretnote.RecycleViewAdapterSearchLogins
import com.example.secretnote.VM_REGg
import com.example.secretnote.chats_k
import com.example.secretnote.databinding.FragmentGalleryBinding
import com.example.secretnote.grpc_method


class GalleryFragment : Fragment() {
    lateinit var recyclerView2: RecyclerView;
    lateinit var recyclerViewAdapter2: RecycleViewAdapterSearchLogins

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false);
        val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java);

        _binding = FragmentGalleryBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        //val textView: TextView = binding.textGallery
        //galleryViewModel.text.observe(viewLifecycleOwner) {
        //    textView.text = it
        //}

        recyclerView2 = view.findViewById<RecyclerView>(R.id.recyclerViewLogins);
        recyclerViewAdapter2 = RecycleViewAdapterSearchLogins(chats_k, VM_REGg.loginList.value!!.toMutableList<String>())
        recyclerView2.adapter = recyclerViewAdapter2
        recyclerView2.layoutManager = LinearLayoutManager(view.context)


        VM_REGg.loginList.observe(this.viewLifecycleOwner) {
            //Log.d("EditTextLogin", "OBESRVERAAAAAAAAAAAAAAAAAAAAAAAAAA")
            recyclerViewAdapter2.updateData(VM_REGg.loginList.value!!.toMutableList<String>())
            recyclerViewAdapter2.notifyDataSetChanged()

        }


        view.findViewById<EditText>(R.id.EditTextSerchLogins).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                sendMsg_gm_obj.getlogin(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })




        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
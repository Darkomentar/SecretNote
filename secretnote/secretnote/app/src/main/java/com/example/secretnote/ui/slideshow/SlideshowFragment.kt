package com.example.secretnote.ui.slideshow

import CryptoTransport
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.secretnote.R
import com.example.secretnote.VM_REGg
import com.example.secretnote.databinding.FragmentSlideshowBinding
import com.example.secretnote.grpc_method
import com.google.protobuf.ByteString

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     //   val view = inflater.inflate(R.layout.fragment_home, container, false)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        var grpc_create : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        var buton = root.findViewById<Button>(R.id.button2)
        buton.setOnClickListener{

            var name_chat=root.findViewById<EditText>(R.id.textView4).text.toString()
            var login_chat=root.findViewById<EditText>(R.id.textView5).text.toString()
            var cript = CryptoTransport()
            var keygen = cript.GenerateKeysString()
            var publicKey =keygen.public

        //    var privateKey =cript.encryptMessage("1".toByteArray(), public_key_creater)
        //    Log.d("recylerChats", "SlideshowFragment4"+ privateKey);
        //    var bs_priv_key = ByteString.copyFrom(privateKey)
        //   var errorCode = grpc_create.create_group_chat(VM_REGg.login.value!!, VM_REGg.password.value!!, name_chat,login_chat,  publicKey, keygen.private.toByteArray() )
            var errorCode = grpc_create.create_group_chat(VM_REGg.login.value!!, VM_REGg.password.value!!, name_chat,login_chat,  publicKey, keygen.private.toByteArray() )
          //  var bs_priv_key = ByteString.copyFrom(privateKey)
      //      Log.d("recylerChats", "SlideshowFragment44"+ bs_priv_key);
         //   var errorCode = grpc_create.create_group_chat(VM_REGg.login.value!!, VM_REGg.password.value!!, name_chat,login_chat,  publicKey, privateKey. )
            Log.d("recylerChats",   keygen.private);
            if (errorCode == 0){
                Toast.makeText(activity, "Успешно", Toast.LENGTH_LONG).show();
                root.findNavController().navigate(R.id.action_nav_slideshow_to_nav_home);
            }
            else if (errorCode==-1)
            {
                Toast.makeText(activity, "Такой чат уже существует", Toast.LENGTH_LONG).show();
                //чат такой уже есть
            }
            else if  (errorCode==-2)
            {
                Toast.makeText(activity, "логин и парль не верен", Toast.LENGTH_LONG).show();
                //логин и парльне верен
            }
            else if (errorCode==-3)
            {
                Toast.makeText(activity, "Нет доступа к серверу", Toast.LENGTH_LONG).show();
                //доступ к серверу
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
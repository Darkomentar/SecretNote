package com.example.secretnote

import CryptoTransport
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.seretnote.SaveReadFile


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [register4.newInstance] factory method to
 * create an instance of this fragment.
 */
class register4 : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        var t : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
        var errorcode = t.emailconfimation(VM_REGg.email.value.toString().toLowerCase());
        //Toast.makeText(activity,errorcode.toString(), Toast.LENGTH_LONG).show()
        Log.d("111223", errorcode.toString());
        if (errorcode == 1)
        {
            Toast.makeText(activity,"Данный email уже зарегестрирован ", Toast.LENGTH_LONG).show()
        }
        if (errorcode == 2)
        {
            Toast.makeText(activity,"На данный email уже отправлен ключ, введите ключ или подождите 5 минут ", Toast.LENGTH_LONG).show()
        }
        if (errorcode == 3)
        {
            Toast.makeText(activity,"Ошибка отправки на почту, проверте доступ к интернету ", Toast.LENGTH_LONG).show()
        }
        if (errorcode == 4)
        {
            Toast.makeText(activity,"Ошибка отправки на почту, проверте данные ", Toast.LENGTH_LONG).show()
        }
        if (errorcode == 0)
        {
            Toast.makeText(activity,"Код отправлен на email", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fun isNumeric(str: String): Boolean {
            return str.matches(Regex("\\d+"))
        }
        val view = inflater.inflate(R.layout.fragment_register_final, container, false)
       // view.findViewById<Button>(R.id.ConfirmButtonnnnnnnnnnn).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_register4_to_login))
        view.findViewById<Button>(R.id.ConfirmButtonnnnnnnnnnn).setOnClickListener{
            var codee = " ";

                codee  =  view.findViewById<EditText>(R.id.editTextEmailCode).text.toString();
                if (isNumeric(codee))
                {
                    VM_REGg.code.value = codee.toInt()
                    var t : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);

                    val crypt = CryptoTransport()
                    val keys = crypt.GenerateKeysString()
                    VM_REGg.publicKey.value = keys.public
                    val Saver = SaveReadFile()
                //    Saver.readFileFromInternalStorage(it1.context, "private.txt")
                    getView()?.let { it1 -> Saver.saveStringToFile(it1.context, "private.txt", keys.private) }
                    //getView()?.let { it1 -> Saver.saveStringToFile(it1.context, "public.txt", keys.public) }
                    var erorrRegistration = t.Registration(VM_REGg.surname.value.toString(),VM_REGg.name.value.toString(),VM_REGg.middle_name.value.toString(),
                        VM_REGg.password.value.toString(),VM_REGg.login.value.toString(),VM_REGg.email.value.toString().toLowerCase(),VM_REGg.code.value!!.toInt(),
                        VM_REGg.publicKey.value.toString())
                    if (erorrRegistration == 1)
                    {
                        Toast.makeText(activity,"Не верный код!!! ", Toast.LENGTH_LONG).show()
                    }
                    if (erorrRegistration == 2)
                    {
                        Toast.makeText(activity,"Ошибка регистрации(на стороне сервера)", Toast.LENGTH_LONG).show()
                    }
                    if (erorrRegistration == 3)
                    {
                        Toast.makeText(activity,"Ошибка отправки запроса, проверте доступ к интернету ", Toast.LENGTH_LONG).show()
                    }
                    if (erorrRegistration == 0 )
                    {
                        Toast.makeText(activity,"Регистрация прошла успешно", Toast.LENGTH_LONG).show()
                        view.findNavController().navigate(R.id.action_register4_to_login)
                    }
                }
                else
               {
                   Toast.makeText(activity,"нужны числа ", Toast.LENGTH_LONG).show()

               }


        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment register4.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            register4().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
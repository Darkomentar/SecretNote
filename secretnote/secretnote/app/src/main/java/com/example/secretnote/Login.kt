package com.example.secretnote
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.room.Room



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // With blank your fragment BackPressed will be disabled.
        }
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.findViewById<Button>(R.id.ReggggButton).setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_login_to_register1))
        view.findViewById<Button>(R.id.loginButton).setOnClickListener{
            var lo =  view.findViewById<EditText>(R.id.editTextUsername).text.toString();
            var pa = view.findViewById<EditText>(R.id.editTextPassword).text.toString();
            VM_REGg.login.value = lo;
            VM_REGg.password.value = pa;
            var t : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
            var errorr = t.login(lo,pa) // 0 - если зашел 1 - если логин или пароль невеный
            if (errorr == 0)
            {

                val db by lazy { Room.databaseBuilder(view.context, AppDatabase::class.java, "lp.db").build(); }
                Toast.makeText(activity,"Свои люди, проходи! ", Toast.LENGTH_LONG).show()

                Thread {
                    db.loginPasswordDao().delete()
                    val lp = LoginPassword(0, lo, pa)
                    db.loginPasswordDao().insert(lp)
                    db.close()
                }.start()

                view.findNavController().navigate(R.id.action_login_to_mainActivity)
            }
            if (errorr == 1)
            {
                Toast.makeText(activity,"Ошибка входа, проверьте логин или пароль ", Toast.LENGTH_LONG).show()
            }
            if (errorr == 3)
            {
                Toast.makeText(activity,"Ошибка входа, проверте доступ к интернету ", Toast.LENGTH_LONG).show()
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
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
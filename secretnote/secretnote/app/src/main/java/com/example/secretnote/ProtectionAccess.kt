package com.example.secretnote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.room.Room
import com.chaos.view.PinView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProtectionAccess.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProtectionAccess : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_protection_access, container, false)

        val change_button = view.findViewById<Button>(R.id.change_pin_button)
        val pinView = view.findViewById<PinView>(R.id.pinview2)

        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Здесь можно выполнить действия до изменения текста в PinView
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Здесь можно выполнить действия при изменении текста в PinView
            }

            override fun afterTextChanged(s: Editable) {
                if(s.count() == 4)
                {
                    // Здесь код типа шо делать если введен пин код
                    change_button.visibility = View.VISIBLE
                    pinView.visibility = View.INVISIBLE
                    Thread {
                        var tt = pinView.text.toString()
                        val db by lazy { Room.databaseBuilder(mainActivityContext, AppDatabase::class.java, "sc.db").build() ;}
                        db.secretCodeDao().delete()
                        val sc = SecretCode(0, tt)
                        db.secretCodeDao().insert(sc)
                        db.close()
                    }.start()
                    Toast.makeText(mainActivityContext,"Пин код успешно изменён", Toast.LENGTH_LONG).show()
                }
            }
        })

        change_button.setOnClickListener {
            pinView.setText("")
            change_button.visibility = View.INVISIBLE
            pinView.visibility = View.VISIBLE

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
         * @return A new instance of fragment ProtectionAccess.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProtectionAccess().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
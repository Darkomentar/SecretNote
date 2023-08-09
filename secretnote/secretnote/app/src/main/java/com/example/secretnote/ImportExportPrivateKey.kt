package com.example.secretnote

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seretnote.SaveReadFile

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImportExportPrivateKey.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImportExportPrivateKey : Fragment() {
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
    fun copyToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = android.content.ClipData.newPlainText("Text", text)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Текст скопирован в буфер обмена", Toast.LENGTH_SHORT).show()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as? AppCompatActivity)?.supportActionBar?.title = "IMPORT/EXPORT PRIVATE KEY"
        var t = inflater.inflate(R.layout.fragment_import_export_private_key, container, false)
        val Saver = SaveReadFile()
        var et:EditText = t.findViewById(R.id.pk)
        et.setText(VM_REGg.privateKey.value!!.toString())
        t.findViewById<Button>(R.id.button3).setOnClickListener {
            copyToClipboard(t.context, et.text.toString())
        }
        t.findViewById<Button>(R.id.change_pin_button).setOnClickListener {
            getView()?.let { it1 -> Saver.saveStringToFile(it1.context, "private.txt", et.text.toString()) }
            VM_REGg.privateKey.value = Saver.readFileFromInternalStorage(mainActivityContext, "private.txt")
            Toast.makeText(context, "Ключ импортирован", Toast.LENGTH_SHORT).show()
        }

        //getView()?.let { it1 -> Saver.saveStringToFile(it1.context, "private.txt", VM_REGg.privateKey.value!!) }
        //VM_REGg.privateKey.value = Saver.readFileFromInternalStorage(mainActivityContext, "private.txt")

        return t
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImportExportPrivateKey.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImportExportPrivateKey().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
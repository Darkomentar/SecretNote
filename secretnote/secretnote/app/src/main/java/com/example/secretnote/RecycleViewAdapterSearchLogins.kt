package com.example.secretnote
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception


class RecycleViewAdapterSearchLogins(private val context: Context, private var itemList: List<String>) : RecyclerView.Adapter<RecycleViewAdapterSearchLogins.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSearchLogins: TextView = view.findViewById(R.id.textViewLoginsSearch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_search_logins, parent, false);
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: RecycleViewAdapterSearchLogins.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textViewSearchLogins.text = item
        holder.itemView.setOnClickListener{
            //VM_REGg = ViewModelProvider(chats_k).get(VM_REG::class.java)
            VM_REGg.messageList.value!!.clear();
            try {
                Log.d("RecyclerLogins", item)
                var sendMsg_gm_obj: grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                VM_REGg.recipientlogin.value = item
                var errorcodee = sendMsg_gm_obj.getmsgs(
                    VM_REGg.login.value!!,
                    VM_REGg.password.value!!,
                    item
                );
            }
            catch(e:Exception)
            {
                Log.d("RecyclerLogins", e.toString())
            }
            holder.itemView.findNavController().navigate(R.id.action_nav_gallery_to_sendMessage2);

        }
    }

    override fun getItemCount(): Int {
        return itemList.size;
    }

    fun updateData(newData: List<String>) {
        itemList = newData;
        notifyDataSetChanged();
    }
}
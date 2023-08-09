package com.example.secretnote
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception


class RecycleViewAdapterSearchAddUserGroup(val context: Context, private var itemList: List<String>) : RecyclerView.Adapter<RecycleViewAdapterSearchAddUserGroup.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSearchLogins: TextView = view.findViewById(R.id.textViewLoginsSearch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_search_logins, parent, false);
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: RecycleViewAdapterSearchAddUserGroup.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textViewSearchLogins.text = item
        holder.itemView.setOnClickListener{
            // принудительно обновляем рекуклер типа

            var sendMsg_gm_obj : grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
            var error = sendMsg_gm_obj.addusergroupchat( holder.textViewSearchLogins.text.toString())
            if (error == -1)
            {
                Toast.makeText(this.context,"Нет такого пользователя", Toast.LENGTH_SHORT).show()
            }
            if (error == -2)
            {
                Toast.makeText(this.context,"Неправильный логин пароль", Toast.LENGTH_SHORT).show()
            }
            if (error == -3)
            {
                Toast.makeText(this.context,"Проблема с доступок к серверу", Toast.LENGTH_SHORT).show()
            }
            if (error == -4)
            {
                Toast.makeText(this.context,"Вы не создатель чата", Toast.LENGTH_SHORT).show()
            }
            if (error == -5)
            {
                Toast.makeText(this.context,"Этот пользователь уже в чате", Toast.LENGTH_SHORT).show()
            }
            if (error == 0)
            {
                Toast.makeText(this.context,"Пользователь успешно добавлен в чат", Toast.LENGTH_SHORT).show()
                if(itemList.size == 1)
                {
                    this.itemList = mutableListOf()
                    this.updateData(mutableListOf())
                }
                sendMsg_gm_obj.getloginsGroupChat(VM_REGg.serchadduserGroupChat.value.toString())
            }

            //holder.itemView.findNavController().navigate(R.id.action_nav_gallery_to_sendMessage2);

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
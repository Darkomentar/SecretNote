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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception


class RecyclerViewAdapter(private val context: Context, private var itemList: List<ChatClass>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewLogin: TextView = view.findViewById(R.id.textViewLogin)
        val textViewFIO: TextView = view.findViewById(R.id.textViewFIO)
        val textViewDontReadMsgCount: TextView = view.findViewById(R.id.textViewDontReadMsgCount)
        val imageViewPhotoProfile: ImageView = view.findViewById(R.id.imageViewProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_chats, parent, false);
        return ViewHolder(view);
    }



    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val item = itemList[position]
        //holder.imageViewPhotoProfile.setImageDrawable(BitmapDrawable(item.photo_profile))
        holder.imageViewPhotoProfile.setImageDrawable(BitmapDrawable(item.photo_profile));
        if(item.dont_read_msg == 0 ) holder.textViewDontReadMsgCount.visibility = View.INVISIBLE; else holder.textViewDontReadMsgCount.visibility = View.VISIBLE;
        holder.textViewDontReadMsgCount.text = item.dont_read_msg.toString();
        if(item.group_chat == true)
        {
            //if(item.dont_read_msg > 0) holder.textViewDontReadMsgCount.text = "";
            //if(item.dont_read_msg == 0) holder.textViewDontReadMsgCount.visibility = View.INVISIBLE;
            holder.textViewLogin.text = "GROUP: " + item.login
            holder.textViewFIO.text = item.last_name
            holder.itemView.setOnClickListener{
                item.dont_read_msg = 0;
                //holder.textViewDontReadMsgCount.text = "";
                holder.textViewDontReadMsgCount.visibility = View.INVISIBLE;
                VM_REGg.messageList.value!!.clear();
                VM_REGg.messageListGroup.value!!.clear();
                try {
                    var sendMsg_gm_obj: grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);

                    VM_REGg.recipientlogin.value = item.login

                    var errorcode1 = sendMsg_gm_obj.get_pu_pr_key_group_chat(
                        VM_REGg.login.value!!,
                        VM_REGg.password.value!!,
                        item.login
                    )
                    Log.d("urgp",errorcode1.toString())
                    if (errorcode1 == -2)
                    {
                        Toast.makeText(this.context,"Нет такого чата",Toast.LENGTH_LONG).show()
                    }
                    else if (errorcode1 == 0)
                    {
                        Log.d("urgp","getmsggroups".toString())
                        var errorcodee = sendMsg_gm_obj.getmsggroups(
                            VM_REGg.login.value!!,
                            VM_REGg.password.value!!,
                            item.login
                        );
                        Log.d("urgp","s".toString())
                    }

                }
                catch(e:Exception)
                {

                }
                holder.itemView.findNavController().navigate(R.id.action_nav_home_to_sendMessageGroup);

            }
        }
        else
        {
            holder.textViewLogin.text = item.login
            holder.textViewFIO.text = item.last_name + " " + item.first_name + " " + item.middle_name
            holder.itemView.setOnClickListener{
                item.dont_read_msg = 0;
                holder.textViewDontReadMsgCount.text = "0";
                VM_REGg.messageList.value!!.clear();
                try {
                    var sendMsg_gm_obj: grpc_method = grpc_method(VM_REGg.ip.value!!, VM_REGg.port.value!!);
                    VM_REGg.recipientlogin.value = item.login
                    var errorcodee = sendMsg_gm_obj.getmsgs(
                        VM_REGg.login.value!!,
                        VM_REGg.password.value!!,
                        item.login
                    );
                }
                catch(e:Exception)
                {

                }
                holder.itemView.findNavController().navigate(R.id.action_nav_home_to_sendMessage2);

            }
        }
//        holder.textViewFIO.text = item.last_name + " " + item.first_name + " " + item.middle_name
//        holder.itemView.setOnClickListener{
//            item.dont_read_msg = 0;
//            holder.textViewDontReadMsgCount.text = "0";
//            VM_REGg.messageList.value!!.clear();
//            try {
//                var sendMsg_gm_obj: grpc_method = grpc_method("94.41.19.40", "50030");
//                VM_REGg.recipientlogin.value = item.login
//                var errorcodee = sendMsg_gm_obj.getmsgs(
//                    VM_REGg.login.value!!,
//                    VM_REGg.password.value!!,
//                    item.login
//                );
//            }
//            catch(e:Exception)
//            {
//
//            }
//            holder.itemView.findNavController().navigate(R.id.action_nav_home_to_sendMessage2);
//
//        }


    }

    override fun getItemCount(): Int {
        return itemList.size;
    }

    fun updateData(newData: List<ChatClass>) {
        itemList = newData;
        notifyDataSetChanged();
    }
}
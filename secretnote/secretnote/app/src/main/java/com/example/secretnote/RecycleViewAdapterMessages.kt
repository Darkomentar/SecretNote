package com.example.secretnote

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView



class RecycleViewAdapterMessages(private val context: Context, private var itemList: List<MessageClass>) : RecyclerView.Adapter<RecycleViewAdapterMessages.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewMessage: TextView = view.findViewById(R.id.textViewLogin);
        val textViewTime: TextView = view.findViewById(R.id.textViewTime);
        val imageViewRead: ImageView = view.findViewById(R.id.imageViewRead);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_messages, parent, false);
        return ViewHolder(view);
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textViewMessage.text = item.message;
        holder.textViewTime.text = item.time;
        if(item.my_message)
        {
            holder.imageViewRead.alpha = 1.0f
            if(item.readed==true)
            {
                holder.imageViewRead.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.read));

            }
            else
            {
                holder.imageViewRead.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.not_read));
            }
            val layoutParams = holder.textViewMessage.layoutParams as ViewGroup.MarginLayoutParams;
            layoutParams.leftMargin = 40;
            layoutParams.rightMargin = 0;
            holder.textViewMessage.layoutParams = layoutParams;
            holder.textViewMessage.gravity = Gravity.END;
            holder.textViewTime.gravity = Gravity.END;

        }
        else
        {

            val layoutParams = holder.textViewMessage.layoutParams as ViewGroup.MarginLayoutParams;
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 40;
            holder.textViewMessage.layoutParams = layoutParams;
            holder.textViewMessage.gravity = Gravity.START;
            holder.textViewTime.gravity = Gravity.START;
            holder.imageViewRead.alpha = 0.0f

        }
    }

    override fun getItemCount(): Int {
        return itemList.size;
    }


    fun updateData(newData: List<MessageClass>) {
        itemList = newData;
        notifyDataSetChanged();
    }
}
package com.example.secretnote
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception


class RecyclerViewAdapterAttendees(private val context: Context, private var itemList: List<AttendeesChat>) : RecyclerView.Adapter<RecyclerViewAdapterAttendees.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val login: TextView = view.findViewById(R.id.login)
        val fio: TextView = view.findViewById(R.id.fio)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_attendees, parent, false);
        return ViewHolder(view);
    }



    override fun onBindViewHolder(holder: RecyclerViewAdapterAttendees.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.login.text = item.login
        holder.fio.text = item.fio



    }

    override fun getItemCount(): Int {
        return itemList.size;
    }

    fun updateData(newData: List<AttendeesChat>) {
        itemList = newData;
        notifyDataSetChanged();
    }
}
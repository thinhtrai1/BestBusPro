package com.app.bestbus.ui.home

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.R
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyTicketAdapter(private val context: Context, private val mTickets: ArrayList<File>) : RecyclerView.Adapter<MyTicketAdapter.ViewHolder>() {
    private val mDateTimeFormat = SimpleDateFormat("HH:mm, EEE, dd MMM yyyy", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rcv_my_ticket, parent, false))
    }

    override fun getItemCount(): Int {
        return mTickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mTickets[position].name.let {
            try {
                holder.tvLeave.text = mDateTimeFormat.format(Date(it.substring(0, 13).toLong()))
                holder.tvId.text = it.substring(13, it.length - 4).toInt().toString()
            } catch (e: Exception) {
                holder.tvLeave.text = context.getString(R.string.unknown)
                holder.tvId.text = context.getString(R.string.unknown)
            }
        }

        holder.itemView.setOnClickListener {
            val imageView = ImageView(context)
            imageView.setImageURI(Uri.fromFile(mTickets[position]))
            Dialog(context, android.R.style.Theme_Black_NoTitleBar).apply {
                setContentView(imageView)
                show()
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val tvId: TextView = view.findViewById(R.id.tvId)
        internal val tvLeave: TextView = view.findViewById(R.id.tvLeave)
    }
}
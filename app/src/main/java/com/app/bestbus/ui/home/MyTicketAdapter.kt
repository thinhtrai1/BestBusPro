package com.app.bestbus.ui.home

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.R
import com.app.bestbus.databinding.ItemRcvMyTicketBinding
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyTicketAdapter(private val context: Context, private val mTickets: ArrayList<File>) : RecyclerView.Adapter<MyTicketAdapter.ViewHolder>() {
    private val mDateTimeFormat = SimpleDateFormat("HH:mm, EEE, dd MMM yyyy", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRcvMyTicketBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return mTickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mTickets[position].name.let {
            try {
                holder.view.tvLeave.text = mDateTimeFormat.format(Date(it.substring(0, 13).toLong()))
                holder.view.tvId.text = it.substring(13, it.length - 4).toInt().toString()
            } catch (e: Exception) {
                holder.view.tvLeave.text = context.getString(R.string.unknown)
                holder.view.tvId.text = context.getString(R.string.unknown)
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

    class ViewHolder(val view: ItemRcvMyTicketBinding) : RecyclerView.ViewHolder(view.root)
}
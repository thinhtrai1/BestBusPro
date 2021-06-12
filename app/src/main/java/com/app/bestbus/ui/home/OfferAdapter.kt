package com.app.bestbus.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.R
import com.app.bestbus.application.Application
import com.app.bestbus.databinding.ItemRcvOfferBinding
import com.app.bestbus.models.Offer
import com.app.bestbus.utils.*

class OfferAdapter : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {
    val offers = ArrayList<Offer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRcvOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return offers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            tvOff.text = Util.formatFloat(offers[position].priceOff).plus("$ ")
            tvCode.text = offers[position].code
            tvCopyCode.setOnClickListener {
                val p = holder.bindingAdapterPosition
                (Application.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .setPrimaryClip(ClipData.newPlainText("copy", offers[p].code))
                offers.removeAt(p)
                notifyItemRemoved(p)
                showToast(getString(R.string.offer_code_copied))
            }
        }
    }

    class ViewHolder(val view: ItemRcvOfferBinding) : RecyclerView.ViewHolder(view.root)
}
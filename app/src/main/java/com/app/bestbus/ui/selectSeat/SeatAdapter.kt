package com.app.bestbus.ui.selectSeat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.databinding.ItemRcvSeatBinding

class SeatAdapter(private val seatQuantity: Int, private val seatBooked: ArrayList<Int>, count: Int) : RecyclerView.Adapter<SeatAdapter.ViewHolder>() {

    val selectingList = ArrayList<Int>()
    private val seatNames = ArrayList<String>()
    lateinit var itemLayout: ViewGroup.LayoutParams

    init {
        for (i in 0 until seatQuantity) {
            var s = i / (count * 2) + 65
            if (s > 90) {
                s += 6
            }
            seatNames.add(s.toChar().toString().plus(i % (count * 2) + 1))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRcvSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return seatQuantity / 2 + seatQuantity % 2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            root.layoutParams = itemLayout
            val p1 = position * 2
            val p2 = position * 2 + 1

            when {
                selectingList.contains(p1) -> {
                    viewSeat1.isSelected = true
                }
                seatBooked.contains(p1) -> {
                    viewSeat1.isEnabled = false
                    viewSeat1.isSelected = false
                }
                else -> {
                    viewSeat1.isEnabled = true
                    viewSeat1.isSelected = false
                }
            }
            tvSeatName1.text = seatNames[p1]
            if (p2 < seatQuantity) {
                tvSeatName2.text = seatNames[p2]
                viewSeat2.visibility = View.VISIBLE
                when {
                    selectingList.contains(p2) -> {
                        viewSeat2.isSelected = true
                    }
                    seatBooked.contains(p2) -> {
                        viewSeat2.isEnabled = false
                        viewSeat2.isSelected = false
                    }
                    else -> {
                        viewSeat2.isEnabled = true
                        viewSeat2.isSelected = false
                    }
                }
            } else {
                viewSeat2.visibility = View.INVISIBLE
            }

            viewSeat1.setOnClickListener {
                if (!seatBooked.contains(p1)) {
                    if (selectingList.contains(p1)) {
                        selectingList.remove(p1)
                        viewSeat1.isSelected = false
                    } else {
                        selectingList.add(p1)
                        viewSeat1.isSelected = true
                    }
                }
            }

            viewSeat2.setOnClickListener {
                if (!seatBooked.contains(p2)) {
                    if (selectingList.contains(p2)) {
                        selectingList.remove(p2)
                        viewSeat2.isSelected = false
                    } else {
                        selectingList.add(p2)
                        viewSeat2.isSelected = true
                    }
                }
            }
        }
    }

    class ViewHolder(val view: ItemRcvSeatBinding) : RecyclerView.ViewHolder(view.root)
}
package com.app.bestbus.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.databinding.BindingAdapter
import com.app.bestbus.R
import com.squareup.picasso.Picasso

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(url: String?) {
        if (url != null) {
            Picasso.get().load(url).placeholder(R.drawable.ic_app).into(this)
        } else {
            setImageResource(R.drawable.ic_app)
        }
    }

    @JvmStatic
    @BindingAdapter("onTextChanged")
    fun EditText.onTextChanged(listener: IOnDataChangedListener) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                listener.onChanged(s.toString())
            }
        })
    }
}
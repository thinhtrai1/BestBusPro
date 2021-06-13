package com.app.bestbus.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import com.app.bestbus.R
import com.squareup.picasso.Picasso

object BindingAdapter {
    @JvmStatic
    @BindingAdapter(value = ["imageUrl", "dimens", "isPlaceHolder"], requireAll = false)
    fun ImageView.loadImage(url: String?, dimens: Float?, isPlaceHolder: Boolean) {
        if (url != null) {
            with(Picasso.get().load(Constant.BASE_URL + url)) {
                if (dimens != null) {
                    resize(dimens.toInt(), dimens.toInt())
                }
                if (isPlaceHolder) {
                    placeholder(R.drawable.ic_app)
                }
                into(this@loadImage)
            }
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
                listener.onChanged(s.toString().trim())
            }
        })
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun View.setVisible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
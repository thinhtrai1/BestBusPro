package com.app.bestbus.ui.searchTour

import androidx.lifecycle.MutableLiveData
import com.app.bestbus.base.BaseViewModel

class SearchTourViewModel : BaseViewModel() {
    var from = ""
    var to = ""
    var date = MutableLiveData("")
}
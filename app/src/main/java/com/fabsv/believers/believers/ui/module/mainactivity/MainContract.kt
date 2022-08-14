package com.fabsv.believers.believers.ui.module.mainactivity

import androidx.fragment.app.Fragment
import com.fabsv.believers.believers.ui.base.MvpView
import com.lv.note.personalnote.ui.base.MvpPresenter

interface MainContract {

    interface MainView : MvpView {
        fun showPermissionRequiredAlert()
    }

    interface MainPresenter : MvpPresenter<MainContract.MainView> {
        fun showFragment()
        fun onCameraPermissionGrantedForScan(currentFragmentInstance: Fragment)
        fun onPermissionDenied()
    }
}

package com.fabsv.believers.believers.ui.module.userdetail

import com.fabsv.believers.believers.data.source.remote.model.UserProfileResponse
import com.fabsv.believers.believers.ui.base.MvpView
import com.lv.note.personalnote.ui.base.MvpPresenter
import io.reactivex.Observable

interface UserDetailContract {
    interface UserDetailView : MvpView {
        fun getRejectButtonClickEvent(): Observable<Any>
        fun getApproveButtonClickEvent(): Observable<Any>
        fun getUserProfile(): UserProfileResponse?
        fun getUserProfileValidity(): Observable<Boolean>
        fun exitUserDetailScreen()
        fun onApproveStatusUpdateFailed()
        fun onApproveStatusUpdateSuccess()
        fun updateApproveButtonEnableStatus(enable: Boolean)
        fun resetScreen()
    }

    interface UserDetailPresenter : MvpPresenter<UserDetailContract.UserDetailView> {
        fun validate()
        fun unSubscribeValidations()

    }
}
package com.fabsv.believers.believers.ui.module.home

import android.app.Activity
import android.content.Context
import com.fabsv.believers.believers.R
import com.fabsv.believers.believers.data.repository.UserRepository
import com.fabsv.believers.believers.data.source.local.prefs.AppPreferencesHelper
import com.fabsv.believers.believers.ui.module.login.LoginFragment
import com.fabsv.believers.believers.ui.module.report.ReportFragment
import com.fabsv.believers.believers.ui.module.scan.ScanFragment
import com.fabsv.believers.believers.util.methods.UtilityMethods
import com.lv.note.personalnote.ui.base.MvpBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger

class HomePresenter(val context: Context, val appPreferencesHelper: AppPreferencesHelper) :
        MvpBasePresenter<HomeContract.HomeView>(), HomeContract.HomePresenter, AnkoLogger {

    private var compositeDisposable: CompositeDisposable
    private var userRepository: UserRepository
    private var homeInteractor = HomeInteractor(context, appPreferencesHelper)

    init {
        this.compositeDisposable = CompositeDisposable()
        this.userRepository = UserRepository(context, appPreferencesHelper)
    }

    override fun validate() {
        logoutButtonObservableHandler()

        scanButtonObservableHandler()

        collectionReportButtonObservableHandler()

        quorumReportButtonObservableHandler()
    }

    override fun showLoggedInUserDetail() {
        appPreferencesHelper.getLoggedInUserUsername()?.let {
            if (it.isNotEmpty() && it.isNotBlank()) {
                showLoggedInUsername(it)
            }
        }
    }

    override fun unSubscribeValidations() {
        clearCompositeDisposable()
    }

    private fun scanButtonObservableHandler() {
        val scanButtonObservable: Observable<Boolean> = getView()!!
                .getScanButtonClickEvent()
                .map { click: Any -> true }
                .doOnNext { clicked: Boolean ->
                    showScanScreen()
                }

        val scanButtonDisposable = scanButtonObservable.subscribe()
        compositeDisposable.add(scanButtonDisposable)
    }

    private fun logoutButtonObservableHandler() {
        val logoutButtonObservable: Observable<Boolean> = getView()!!
                .getLogoutButtonClickEvent()
                .map { click: Any -> true }
                .switchMap { logoutClicked: Boolean ->
                    userRepository.onLogoutClicked()
                }
                .doOnNext { logoutStatus: Boolean ->
                    updateLogoutStatus(logoutStatus)
                }
        val logoutButtonDisposable = logoutButtonObservable.subscribe()
        compositeDisposable.add(logoutButtonDisposable)
    }

    private fun collectionReportButtonObservableHandler() {
        getView()?.let {
            /*compositeDisposable.add(it.getCollectionReportButtonClickEvent()
                    .map {
                        getView()?.showProgress()
                    }
                    .doOnNext {
                        compositeDisposable.add(homeInteractor.getCollectionReport()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            getView()?.hideProgress()
                                            if (it.isSuccessful()) {
                                                it.data?.let {
                                                    showCollectionReportScreen(it)
                                                }
                                            } else {
                                                onApiRequestFailure()
                                            }
                                        },
                                        {
                                            apiRequestException()
                                        }
                                ))
                    }
                    .subscribe())*/

            compositeDisposable.add(it.getCollectionReportButtonClickEvent()
                    .map {
                        getView()?.hideSoftKeyboard()
                    }
                    .flatMap {
                        UtilityMethods.isConnected(context as Activity)
                    }
                    .map {
                        getView()?.showProgress()
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        homeInteractor.getCollectionReport()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                getView()?.hideProgress()
                                if (it.isSuccessful()) {
                                    it.data?.let {
                                        showCollectionReportScreen(it)
                                    }
                                } else {
                                    onApiRequestFailure()
                                }
                            },
                            {
                                apiRequestException(it)
                            }
                    ))
        }
    }

    private fun quorumReportButtonObservableHandler() {
        getView()?.let {
            /*compositeDisposable.add(it.getQuorumReportButtonClickEvent()
                    .map {
                        getView()?.showProgress()
                    }
                    .doOnNext {
                        compositeDisposable.add(homeInteractor.getQuorumReport()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            getView()?.hideProgress()
                                            if (it.isSuccessful()) {
                                                it.data?.let {
                                                    showCollectionReportScreen(it)
                                                }
                                            } else {
                                                onApiRequestFailure()
                                            }
                                        },
                                        {
                                            apiRequestException()
                                        }
                                ))
                    }
                    .subscribe())*/

            compositeDisposable.add(it.getQuorumReportButtonClickEvent()
                    .map {
                        getView()?.hideSoftKeyboard()
                    }
                    .flatMap {
                        UtilityMethods.isConnected(context as Activity)
                    }
                    .map {
                        getView()?.showProgress()
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        homeInteractor.getQuorumReport()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                getView()?.hideProgress()
                                if (it.isSuccessful()) {
                                    it.data?.let {
                                        showCollectionReportScreen(it)
                                    }
                                } else {
                                    onApiRequestFailure()
                                }
                            },
                            {
                                apiRequestException(it)
                            }
                    ))
        }
    }

    private fun onApiRequestFailure() {
        getView()?.showShortToast(context.getString(R.string.report_fetch_failed))
        getView()?.resetScreen()
    }

    private fun apiRequestException(throwable: Throwable) {
        getView()?.hideProgress()
        getView()?.resetScreen()
        if (context.getString(R.string.not_connected_to_network).equals(throwable.message, ignoreCase = true)) {
            getView()?.showShortToast(context.getString(R.string.not_connected_to_network))
        } else {
            getView()?.showShortToast(context.getString(R.string.something_went_wrong_please_contact_admin))
        }
    }

    private fun showLoggedInUsername(username: String) {
        if (isViewAttached()) {
            getView()?.showLoggedInUsername(username)
        }
    }

    private fun updateLogoutStatus(logoutStatus: Boolean) {
        if (isViewAttached()) {
            if (logoutStatus) {
                getView()?.showFragment(LoginFragment.getInstance(), false)
            } else {
                getView()?.showShortToast(context.getString(R.string.something_went_wrong_please_contact_admin))
            }
        }
    }

    private fun showScanScreen() {
        if (isViewAttached()) {
            getView()?.showFragment(ScanFragment.getInstance(), true)
        }
    }

    private fun showCollectionReportScreen(collectionReportResponse: Any) {
        if (isViewAttached()) {
            getView()?.resetScreen()
            getView()?.showFragment(ReportFragment.getInstance(collectionReportResponse), true)
        }
    }

    private fun clearCompositeDisposable() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    override fun detachView(retainInstance: Boolean) {
        clearCompositeDisposable()
        super.detachView(retainInstance)
    }
}
package com.fabsv.believers.believers.ui.base

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.fabsv.believers.believers.App
import com.fabsv.believers.believers.R
import com.fabsv.believers.believers.data.source.local.prefs.AppPreferencesHelper
import com.fabsv.believers.believers.ui.utils.ProgressDialog
import com.fabsv.believers.believers.ui.utils.setCustomToastProperties
import com.fabsv.believers.believers.util.constants.AppConstants.ApiConstants.Companion.baseUrl
import com.fabsv.believers.believers.util.methods.UtilityMethods
import com.lv.note.personalnote.ui.base.MvpPresenter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

/**
 * Created by vishnu.benny on 2/20/2018.
 */
abstract class MvpActivity<V : MvpView, P : MvpPresenter<V>> : BaseActivity(), MvpView {

    protected var presenter: P? = null
    private lateinit var appPreferenceHelper: AppPreferencesHelper
    private var progress: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        registerPresenter()

        @Suppress("UNCHECKED_CAST")
        presenter!!.attachView(this as V)
        prepareAppConstants()
        onPrepareActivity()
    }

    private fun prepareAppConstants() {
        baseUrl = appPreferenceHelper.getAppBaseUrl()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter!!.detachView(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }

    protected abstract fun onPrepareActivity()

    private fun registerPresenter() {
        if (null == presenter) {
            presenter = createPresenter()
        }
    }

    override fun showProgress() {
        if (null == progress) {
            progress = ProgressDialog.progressDialog(this)
        }
        progress?.show()
    }

    override fun hideProgress() {
        progress?.let {
            if (it.isShowing) {
                it.hide()
            }
        }
    }

    override fun showShortToast(message: String) {
        toast(message)
    }

    override fun showFailedShortToast(message: String) {
        showCustomToast(message, textColor = Color.RED, bgColor = Color.GRAY)
    }

    override fun showSuccessShortToast(message: String) {
        showCustomToast(message, textColor = Color.GREEN, bgColor = Color.GRAY)
    }

    override fun showFragment(fragment: Fragment, isAddToBackStack: Boolean) {
        if (isAddToBackStack) {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                    .addToBackStack(fragment.tag).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }

    override fun hideSoftKeyboard() {
        UtilityMethods.hideKeyboard(this as Activity)
    }


    override fun getAppPreferencesHelper(): AppPreferencesHelper {
        appPreferenceHelper = (applicationContext as App).getAppPreferencesHelper()
        return appPreferenceHelper
    }

    override fun popBackCurrentFragment(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissionsSafely(permissionArray: Array<String>, requestId: Int) {
        requestPermissions(permissionArray, requestId)
    }

    override fun safeFinishActivity() {
        while (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
        finish()
    }

    override fun updateToolbarTitle(title: String?, homeUpEnabled: Boolean) {
        title?.let {
            toolbar.title = title
            setSupportActionBar(toolbar)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(homeUpEnabled)
    }

    fun getCurrentFragmentInstance(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.container)
    }

    private fun showCustomToast(message: String, textColor: Int = Color.WHITE, bgColor: Int? = null) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setCustomToastProperties(textColor = textColor, bgColor = bgColor)
        toast.apply { show() }
    }

    protected abstract fun createPresenter(): P?

    protected abstract fun getLayoutResId(): Int

}

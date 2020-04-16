package com.example.currentweather

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.example.currentweather.models.BaseResponse
import com.example.currentweather.ui.BaseFragment
import com.example.currentweather.ui.FragmentDetails
import com.example.currentweather.ui.FragmentForecast
import com.example.currentweather.util.Logger
import com.example.currentweather.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val preferenceHelper: PreferenceHelper by inject()
    private val input by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private var fragmentType: String = preferenceHelper.getFragmentType() ?: FragmentDetails::class.java.simpleName
    companion object {
        private const val EDIT_TEXT = "edit_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.log("MainActivity", "onCreate")

        subscribe()
        setListeners()
        updateFragment()
        if (savedInstanceState == null) getFragment().requestUpdate(null)
        else if (savedInstanceState.getBoolean(EDIT_TEXT)) showEditCity()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(EDIT_TEXT, edit_city.visibility == View.VISIBLE)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (edit_city.visibility == View.VISIBLE) {
            showCityLabel()
        } else {
            super.onBackPressed()
        }
    }

    private fun subscribe() {
        mainViewModel.isLoadingNow.observe(this, Observer {
            container.isRefreshing = it
        })
        mainViewModel.errorStringRes.observe(this, Observer { stringRes ->
            error_text.setText(stringRes)
        })
    }

    private fun setListeners() {
        city_name.setOnClickListener {
            showEditCity()
        }
        edit_city.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                && keyEvent.action == KeyEvent.ACTION_DOWN
            ) {
                showCityLabel()
                return@setOnKeyListener true
            }
            false
        }
        layout.setOnClickListener {
            if (edit_city.visibility == View.VISIBLE) {
                showCityLabel()
            }
        }
        switch_view.setOnClickListener {
            if (fragmentType == FragmentForecast::class.java.simpleName)
                fragmentType = FragmentDetails::class.java.simpleName
            else if (fragmentType == FragmentDetails::class.java.simpleName)
                fragmentType = FragmentForecast::class.java.simpleName
            Logger.log("MainActivity", "setListeners switch to $fragmentType")
            city_name.text.toString().trim().also {
                updateFragment(it)
                getFragment().requestUpdate(it)
            }
            preferenceHelper.saveFragmentType(fragmentType)
        }
        container.setOnRefreshListener {
            getFragment().requestUpdate(city_name.text.toString().trim())
        }
    }

    private fun showCityLabel() {
        city_name.visibility = View.VISIBLE
        edit_city.visibility = View.GONE
        layout.setBackgroundResource(R.color.background)
        input.hideSoftInputFromWindow(container.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        getFragment().changeCity(edit_city.text.toString().trim())
        Logger.log("MainActivity", "showCityLabel type: $fragmentType, ${edit_city.text}")
    }

    private fun showEditCity() {
        city_name.visibility = View.GONE
        edit_city.setText(city_name.text)
        edit_city.visibility = View.VISIBLE
        edit_city.requestFocus()
        edit_city.selectAll()
        layout.setBackgroundResource(R.color.shadow_background)
        input.showSoftInput(edit_city, InputMethodManager.SHOW_IMPLICIT)
        Logger.log("MainActivity", "showEditCity type: $fragmentType, ${edit_city.text}")
    }

    private fun updateFragment(cityName: String? = null) { // set new Fragment
        Logger.log("MainActivity", "updateFragment $fragmentType")
        val newFragment = if (fragmentType == FragmentForecast::class.java.simpleName)
            FragmentForecast()
        else FragmentDetails()
        supportFragmentManager.replaceFragment(newFragment)

        getFragment().observe(cityName, city_name)
    }

    private fun getFragment() =
        (supportFragmentManager.findFragmentById(R.id.layout) as BaseFragment<BaseResponse>)

    private fun FragmentManager.replaceFragment(newFragment: BaseFragment<*>) {
        Logger.log("MainActivity", "replaceFragment new: ${newFragment::class.java.simpleName}")
        val transaction = beginTransaction()
        transaction.replace(R.id.layout, newFragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.commitNow()
    }

}

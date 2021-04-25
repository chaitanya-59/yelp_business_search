package com.beer.startup.flows

//import androidx.lifecycle.ViewModel
import com.beer.startup.BuildConfig
//import com.beer.startup.repositories.UserSettingsRepository

class SplashViewModel {

    lateinit var mView: IView
    //val userSettingsRepo = UserSettingsRepository()

    /////////////////////////////////////       IViewModel      //////////////////////////////////
    fun init(view: IView) {
        mView = view
        //userSettingsRepo.updateBaseUrl(BuildConfig.BASE_ENDPOINT, BuildConfig.SUBSCRIPTION_KEY)
        mView.requestLocation()
    }

    fun handleLocationPermissionGranted() {
        mView.showLoginScreen()
    }

    fun handleLocationPermissionDenied() {
        mView.showLoginScreen()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
}


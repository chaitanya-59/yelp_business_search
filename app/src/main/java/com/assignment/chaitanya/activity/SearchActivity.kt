package com.assignment.chaitanya.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.chaitanya.R
import com.assignment.chaitanya.flows.adapters.NearByAdapter
import com.assignment.chaitanya.flows.adapters.OnItemClicked
import com.assignment.chaitanya.flows.pojo.BusinessesList
import com.assignment.chaitanya.flows.webservice.RetrofitController
import com.assignment.chaitanya.utils.checkLocationPermission
import com.assignment.chaitanya.utils.getProgressDialog
import com.assignment.chaitanya.utils.showToast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity: AppCompatActivity(), SearchView.OnQueryTextListener, LocationListener, OnItemClicked {
    private lateinit var mLocationManager: LocationManager
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocation: Location? = null
    private var mBusinessesList: BusinessesList? = null

    @Override override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (checkLocationPermission()) {
            createLocationRequest()
            initializeCurrentLocationTracker()
            getLocation()
        }
        mProgressDialog =  getProgressDialog()
        search_view.onActionViewExpanded()
        search_view.setOnQueryTextListener(this)
    }

    private fun moveToDetailActivity(query: CharSequence?) {
        if (query.isNullOrEmpty()) {
            showToast(getString(R.string.valid_input))
        } else {
            getNearByBusiness(query.toString())
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        moveToDetailActivity(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun getNearByBusiness(key: String?) {
        key?.let { it ->
            if (mLocation != null) {
                mProgressDialog.show()
                RetrofitController.loadBusiness(it, mLocation?.latitude.toString(), mLocation?.longitude.toString(), object : Callback<BusinessesList> {
                    override fun onResponse(call: Call<BusinessesList>?, response: Response<BusinessesList>?) {
                        if (response != null && response.isSuccessful && !response.body()?.businessesList.isNullOrEmpty()) {
                            val nearByBusiness = response.body()
                            mBusinessesList = nearByBusiness
                            nearby_loc_rv.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
                            nearby_loc_rv.setHasFixedSize(true)
                            nearby_loc_rv.adapter = NearByAdapter(nearByBusiness.businessesList, this@SearchActivity)
                        } else {
                            showToast("No result found for your selected location")
                        }
                        mProgressDialog.dismiss()
                    }

                    override fun onFailure(call: Call<BusinessesList>?, t: Throwable?) {
                        mProgressDialog.dismiss()
                        showToast("Please check your internet")
                    }

                })
            } else {
                showToast("We are unable to get your location, please try again")
            }
        }
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (locationRequest != null) {
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                getLastKnownLocation()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(this@SearchActivity,
                                12)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    /**
     * call this method for receive location
     * get location and give callback when successfully retrieve
     * function itself check location permission before access related methods
     *
     */
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        initializeCurrentLocationTracker()
    }

    private fun initializeCurrentLocationTracker() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) {
            initializeLocationListener()
        }
    }

    private fun initializeLocationListener() {
        if (checkLocationPermission()) {
            mFusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        mLocation = location
                    }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000, 15f, this)
        }
    }

    override fun onLocationChanged(loc: Location) {
        mLocation = loc
        mLocationManager?.removeUpdates {  }
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onItemClicked(position: Int) {
        mBusinessesList?.let {
            val detailIntent = Intent(this@SearchActivity, MainActivity :: class.java)
            val bundle = Bundle()
            bundle.putParcelable(MainActivity.BUSINESS_DETAIL, it.businessesList[position])
            detailIntent.putExtra(MainActivity.BUNDLE, bundle)
            startActivity(detailIntent)
        }
    }
}
package com.maropost.management.cab.view.fragments

import android.Manifest
import androidx.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.kotlinpermissions.KotlinPermissions
import com.maropost.management.commons.fragments.MPBaseFragment
import com.maropost.management.cab.viewmodel.MapsViewModel
import com.maropost.management.R
import com.google.android.gms.maps.model.MarkerOptions
import com.maropost.management.cab.utils.LatLngInterpolator
import com.maropost.management.cab.utils.MarkerAnimation


class MapFragment : MPBaseFragment(), OnMapReadyCallback {

    private var mView : View?= null
    private val REQUEST_CHECK_SETTINGS = 100
    private var mGoogleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    //private val GEOFENCE_RADIUS : Float = 500F
    private lateinit var geofencingClient :GeofencingClient
    //private var mapCircle : Circle ? = null
    //private var yourLocationMarker : MarkerOptions ?= null
    private lateinit var points : ArrayList<LatLng>
    private var firstTimeFlag: Boolean = true
    private val mapsViewModel = MapsViewModel()
    private var originLatLng : LatLng ?= null
    private var destLatLng : LatLng ?= null

    enum class REQUEST_TYPE{
        PICKUP,
        DROP
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(mView == null) {
            mView = inflater.inflate(R.layout.maps_fragment, container, false)
            points = ArrayList()
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            geofencingClient = LocationServices.getGeofencingClient(activity!!)
            mapFragment?.getMapAsync(this)
        }
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("")
        lockNavigationDrawer(false)
        showToolbar(true)
        removeToolbarIconLayout()
        observeLiveDataChanges()
        initialiseListeners()
    }

    override fun onResume() {
        super.onResume()
        mapFragment?.onResume()
        //requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mapFragment?.onPause()
        //removeLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        checkForLocationPermission()

        mGoogleMap?.setOnMapClickListener {
            mapsViewModel.endRippleAnimation()
            MarkerAnimation.animateMarkerToGB(mCurrLocationMarker!!, it, LatLngInterpolator.Spherical())
             //animateCamera(it!!)
           // displayRippleAnimation(it)
        }
    }

    private fun observeLiveDataChanges(){
        mapsViewModel.currentLocation.observe(this, Observer { location ->
            if (firstTimeFlag && mGoogleMap != null) {
                firstTimeFlag = false
                originLatLng = LatLng(location!!.latitude, location.longitude)
                showMarker(originLatLng!!)
                displayRippleAnimation(originLatLng!!)
                animateCamera(originLatLng!!)
                removeLocationUpdates()
            }
        })

        /* mapsViewModel.mLineOptions.observe(this, Observer { lineOptions ->
             if(mGoogleMap != null)
                 mGoogleMap?.addPolyline(lineOptions)
         })*/
    }

    private fun initialiseListeners() {
        /*  lnrPickup.setOnClickListener{
              openSearchFragment(REQUEST_TYPE.PICKUP)
          }
          lnrDrop.setOnClickListener{
              openSearchFragment(REQUEST_TYPE.DROP)
          }
          btnRideNow?.setOnClickListener{
              btnRideNow?.visibility = View.INVISIBLE
              mapsViewModel.getDirectionsUrl(originLatLng, destLatLng)
          }*/

    }

    private fun checkForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                requestLocationUpdates()
            } else{
                KotlinPermissions.with(activity!!) // where this is an FragmentActivity instance
                    .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .onAccepted { permissions ->
                        //List of accepted permissions
                        requestLocationUpdates()
                    }
                    .ask()
            }
        } else {
            requestLocationUpdates()
        }
    }

    /**
     * Get current location
     */
    private fun requestLocationUpdates(){
        if(mGoogleMap != null)
            mapsViewModel.requestLocationUpdates(activity!!,mFusedLocationClient,mGoogleMap!!,REQUEST_CHECK_SETTINGS)
    }


    private fun animateCamera(latLng: LatLng) {
        if(mGoogleMap != null) {
            //val latLng = LatLng(location.latitude, location.longitude)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)))
        }
    }

    private fun getCameraPositionWithBearing(latLng: LatLng):CameraPosition {
        return CameraPosition.Builder().target(latLng).zoom(16f).build()
    }

    /**
     * Set marker on specific location
     */
    private fun showMarker(latLng: LatLng) {
        if (mCurrLocationMarker == null)
            mCurrLocationMarker = mGoogleMap?.addMarker(MarkerOptions().
                icon(BitmapDescriptorFactory.defaultMarker()).position(latLng)
                .title("Driver")
                .snippet("Manjeet Singh"))
//        else
//            MarkerAnimation.animateMarkerToGB(mCurrLocationMarker!!, latLng, LatLngInterpolator.Spherical())
    }

    private fun displayRippleAnimation(latLng: LatLng){
        mapsViewModel.displayRippleAnimation(mGoogleMap!!, latLng, activity!!)

    }

    private fun removeLocationUpdates() {
        if (mFusedLocationClient != null)
            mapsViewModel.removeLocationUpdates(mFusedLocationClient!!)
    }

}
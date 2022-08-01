package com.openclassrooms.firebaseREM

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.openclassrooms.firebaseREM.model.Property
import com.openclassrooms.firebaseREM.viewmodel.MainViewModel
import java.io.IOException
import java.util.*

class Map : Fragment(), OnMapReadyCallback {
    private var twoPane: Boolean = false
    private var mViewModel: MainViewModel? = null
    private var mMap: GoogleMap? = null
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.map_fragment, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (mMap == null) {
                    return
                }
                mMap!!.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                )
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(8F))
                mViewModel?.updateLocation(location)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                50f,
                locationListener as LocationListener
            )
        } catch (e: SecurityException) {
            Log.d("logmap", "requestlocationupdateerror")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        if (activity?.findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            twoPane = true
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableMyLocation()
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                10f,
                locationListener!!
            )
        } catch (e: SecurityException) {
            Log.d("logmap", "requestlocationupdateerror")
        }
        val propertyList: MutableList<com.openclassrooms.firebaseREM.model.Property?> = ArrayList()
        mViewModel?.Propertys?.observe(this) {
            if (it != null) {
                propertyList.addAll(it)
            }
            for (property in propertyList) {
                val marker = mMap!!.addMarker(MarkerOptions()
                    .position(getLatLangFromAddress(property?.address + property?.city)!!)
                )
                marker?.tag = property
            }

            mMap?.setOnMarkerClickListener { m: Marker? ->
                val item = m?.tag as Property?
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putSerializable(ItemDetailFragment.ARG_ITEM_ID, item)
                        }
                    }
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.map, fragment)
                        .commit()
                    false
                } else {
                    val intent = Intent(activity, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item)
                    }
                    startActivity(intent)
                    false
                }
            }

        }

    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    mMap!!.isMyLocationEnabled = true
                    mMap!!.uiSettings.isMyLocationButtonEnabled = true
                } catch (e: SecurityException) {
                    Log.e("Exception: %s", Objects.requireNonNull(e.message).toString())
                }
            }
        }
    }

    fun getLatLangFromAddress(strAddress: String?): LatLng? {
        val coder = Geocoder(requireContext(), Locale.getDefault())
        val address: List<Address>?
        return try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return LatLng(-10000.0, -10000.0)
            }
            val location: Address = address[0]
            LatLng(location.getLatitude(), location.getLongitude())
        } catch (e: IOException) {
            LatLng(-10000.0, -10000.0)
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}
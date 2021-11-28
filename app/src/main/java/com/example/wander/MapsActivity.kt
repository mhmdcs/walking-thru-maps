package com.example.wander

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wander.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    //MapsActivity.kt file instantiates the SupportMapFragment in the onCreate() method and
    //uses the class's getMapAsync() to automatically initialize the maps system and the view.
    //The activity that contains the SupportMapFragment must implement the OnMapReadyCallback interface
    //and that interface's onMapReady() method. The onMapReady() method is called when the map is loaded.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //onMapReady is called when the map is ready to be used and provides a non-null instance of GoogleMap
    //this method will only be triggered when the user has installed GooglePlay Services
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in your home and move the camera (the camera = the screen)
        val latitude = 24.779485
        val longitude = 46.627675
        val homeLatLng = LatLng(latitude,longitude)
        val zoom = 18f
        val overlayZoom = 100f //set width in meters for the desired overlay, in this case androidOverlay object

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng,zoom))
        map.addMarker(
            MarkerOptions()
            .position(homeLatLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
            .position(homeLatLng,overlayZoom)

        map.addGroundOverlay(androidOverlay)

        setOnMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)

        enableMyLocation()
    }

    //Override the onRequestPermissionsResult() method.
    //If the requestCode is equal to REQUEST_LOCATION_PERMISSION permission is granted,
    //and if the grantResults array is non empty with PackageManager.PERMISSION_GRANTED
    //in its first slot, call enableMyLocation():
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Check if location permissions are granted and if so enable the location data layer.
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                enableMyLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.maps_options ,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //this is a method stub
    //it takes a GoogleMap as an argument, and attaches a long click listener to the map object
    private fun setOnMapLongClick(map: GoogleMap){

        //this is a lambda, when "it" or -> appear, it is a lambda
        map.setOnMapLongClickListener { latLong ->

            //A snippet is additional text that is displayed below the title.
            //In your case the snippet displays the latitude and longitude of a marker.
            val snippet = String.format(
                Locale.getDefault(),
             //   getString(R.string.lat_long_snippet),
                "Lat: %1$.5f, Long: %2$.5f",
                latLong.latitude,
                latLong.longitude
            )

            map.addMarker(MarkerOptions()
                .position(latLong)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    //method stub in MapsActivity called setPoiClick() that takes a GoogleMap as an argument.
    //In the setPoiClick() method, set an OnPoiClickListener on the passed-in GoogleMap
    private fun setPoiClick(map: GoogleMap){
        //In the onPoiClick() method, place a marker at the POI location.
        //Set the title to the name of the POI. Save the result to a variable called poiMarker.
        map.setOnPoiClickListener { poi ->
        val poiMarker = map.addMarker(
            MarkerOptions()
                .position(poi.latLng)
                .title(poi.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        )
            poiMarker?.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap){

    // Customize the styling of the base map using a JSON object defined
    // in a raw resource file.
    try {
      val success = map.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
      )
        //If the styling is unsuccessful, print a log that the parsing has failed.
        if (!success){
            Log.e(TAG, "Style parsing failed.")
        }
    }
    //In the catch block if the file can't be loaded, the method throws a Resources.NotFoundException.
    catch(e: Resources.NotFoundException) {
        Log.e(TAG, "Can't find style. Error: $e")
    }
    }


    //To check if permissions are granted, create a method in the MapsActivity.kt called isPermissionGranted().
    //In this method, check if the user has granted the permission.
    //it returns true if the user granted the permission, and false if he didn't
    private fun isPermissionGranted(): Boolean {
        return ContextCompat
            .checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    //To enable location tracking in your app, create a method in the MapsActivity.kt
    //called enableMyLocation() that takes no arguments and doesn't return anything.
    //Check for the ACCESS_FINE_LOCATION permission. If the permission is granted,
    //enable the location layer. Otherwise, re-request the permission:
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if(isPermissionGranted()){
            map.setMyLocationEnabled(true)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

}
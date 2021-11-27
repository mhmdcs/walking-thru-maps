package com.example.wander

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wander.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

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
        val latitude = 24.7810910
        val longitude = 46.6223080
        val homeLatLng = LatLng(latitude,longitude)
        val zoom = 15f

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng,zoom))
        map.addMarker(MarkerOptions().position(homeLatLng))

        setOnMapLongClick(map)
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
            )
        }
    }

}
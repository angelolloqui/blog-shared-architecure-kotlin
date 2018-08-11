package com.playtomic.general.manager.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.anemonesdk.general.IContextProvider
import com.anemonesdk.general.client.IHttpClient
import com.anemonesdk.general.json.JSONTransformer
import com.anemonesdk.general.promise.Promise
import com.anemonesdk.model.Address
import com.anemonesdk.model.Coordinate
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.playtomic.general.manager.location.model.Location
import com.playtomic.general.manager.location.model.LocationServiceRequest
import com.playtomic.general.manager.location.model.LocationServiceStatus
import com.playtomic.general.manager.location.model.PlaceAutocomplete
import com.playtomicui.utils.PermissionActivity
import java.util.*
import java.util.concurrent.TimeoutException

/**
 * Created by agarcia on 23/12/2016.
 */

class LocationManager(private val contextProvider: IContextProvider, private val geocoder: Geocoder, private val httpClient: IHttpClient, private val googleApiKey: String)
    : ILocationManager, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val locationApiClient: GoogleApiClient

    private val requests: MutableList<LocationServiceRequest>


    init {
        this.requests = ArrayList<LocationServiceRequest>()
        this.locationApiClient = GoogleApiClient.Builder(contextProvider.applicationContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        locationApiClient.connect()
    }

    override val lastLocation: Location?
        get() {
            if (!locationApiClient.isConnected) {
                return null
            }
            try {
                val location = LocationServices.FusedLocationApi.getLastLocation(locationApiClient)
                        ?: return null
                return Location(location)
            } catch (ex: SecurityException) {
                return null
            }

        }

    override val locationStatus: LocationServiceStatus
        get() {
            if (!isLocationEnabled) {
                return LocationServiceStatus.DISABLED
            }
            if (ContextCompat.checkSelfPermission(contextProvider.applicationContext, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                return LocationServiceStatus.AUTHORIZED
            }
            if (!canRequestPermission()) {
                return LocationServiceStatus.DENIED
            }
            return LocationServiceStatus.NOT_DETERMINED
        }

    override fun findLocation(allowRequestPermission: Boolean): Promise<Location> =
            findLocation(allowRequestPermission, DEFAULT_MIN_ACCURACY.toDouble(), DEFAULT_MAX_AGE.toDouble(), DEFAULT_TIMEOUT.toDouble())

    override fun findLocation(allowRequestPermission: Boolean, minAccuracy: Double, maxAge: Double, timeout: Double): Promise<Location> =
            Promise { fulfill, reject ->
                val request = LocationServiceRequest(minAccuracy, maxAge, timeout, fulfill, reject)
                addRequest(request, allowRequestPermission)
            }

    override fun findAddress(coordinate: Coordinate): Promise<Address> =
            Promise(executeInBackground = true) { fulfill, _ ->
                val placemarks = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
                val addresses = placemarks.map { Address(it, null) }
                fulfill(addresses[0])
            }


    override fun findAddresses(text: String): Promise<List<Address>> =
            Promise(executeInBackground = true) { fulfill, reject ->
                val placemarks = geocoder.getFromLocationName(text, 20)
                val addresses = placemarks.map { Address(it, null) }
                fulfill(addresses)
            }


    override fun hasPermission(): Boolean =
            locationStatus == LocationServiceStatus.AUTHORIZED


    // ****** GOOGLE PLACES *** /

    override fun findAddress(placeAutocomplete: PlaceAutocomplete): Promise<Address> {

        val endpoint = "https://maps.googleapis.com/maps/api/place/details/json"

        val params = HashMap<String, Any>()
        params.put("key", googleApiKey)
        params.put("placeid", placeAutocomplete.id)

        return httpClient.get(endpoint, params).then(JSONTransformer(Address::class.java)::mapObject)
    }

    override fun findAutocomplete(text: String): Promise<List<PlaceAutocomplete>> {

        val endpoint = "https://maps.googleapis.com/maps/api/place/autocomplete/json"

        val params = HashMap<String, Any>()
        params.put("key", googleApiKey)
        params.put("input", text)
        params.put("types", "geocode")

        val location = lastLocation
        if (location != null) {
            params.put("location", location.coordinate)
            params.put("radius", 100000)
        }

        val transformer = JSONTransformer(PlaceAutocomplete::class.java)
        transformer.rootKey = "predictions"
        return httpClient.get(endpoint, params).then(transformer::mapArray)
    }

    override fun requestLocationPermission() {
        if (!hasPermission() && canRequestPermission()) {
            PermissionActivity.requestPermissions(contextProvider.currentActivity, arrayOf(LOCATION_PERMISSION), this::onPermissionResult)
        }
    }

    // ****** INTERNAL *** /

    private fun addRequest(request: LocationServiceRequest, allowRequestPermission: Boolean) {
        val status = locationStatus

        //If location is rejected, then return error
        if (status === LocationServiceStatus.DENIED || status === LocationServiceStatus.DISABLED) {
            request.reject(SecurityException())
            return
        }

        //If last location is already valid then early return
        val lastLocation = lastLocation
        if (lastLocation != null && request.isLocationValid(lastLocation)) {
            request.fulfill(lastLocation)
            return
        }

        //If no permission given yet, then just request it
        if (status === LocationServiceStatus.NOT_DETERMINED) {
            if (!allowRequestPermission) {
                request.reject(SecurityException())
                return
            }
            requestLocationPermission()

            scheduleRequestTimeout(request)
            synchronized(requests) {
                requests.add(request)
            }
            return
        }

        scheduleRequestTimeout(request)

        try {
            startLocationUpdates(request)
            synchronized(requests) {
                requests.add(request)
            }
        } catch (ex: SecurityException) {
            request.reject(ex)
        }

    }

    private fun scheduleRequestTimeout(request: LocationServiceRequest) {

        //If timeout, set timer to be able to fail request after timeout
        if (request.timeout > 0) {
            request.timer = object : TimerTask() {
                override fun run() {
                    requestTimeOut(request)
                }
            }
            Timer().schedule(request.timer, (request.timeout * 1000).toLong())
        }
    }

    private fun canRequestPermission(): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(contextProvider.currentActivity, LOCATION_PERMISSION)
    }

    private val isLocationEnabled: Boolean
        get() {
            val locationManager = contextProvider.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun requestTimeOut(request: LocationServiceRequest) {
        synchronized(requests) {
            if (requests.contains(request)) {
                requests.remove(request)
                request.reject(TimeoutException())
            }
            if (requests.isEmpty()) {
                stopLocationUpdates()
            }
        }
    }

    private fun rejectPendingRequests(error: Throwable) {
        synchronized(requests) {
            val requests = ArrayList(this.requests)
            this.requests.removeAll(requests)
            for (request in requests) {
                request.reject(error)
            }
            stopLocationUpdates()
        }
    }

    @Throws(SecurityException::class)
    private fun startLocationUpdates(request: LocationServiceRequest) {
        if (locationApiClient.isConnected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    locationApiClient,
                    request.locationRequest,
                    this)
        }
    }

    private fun stopLocationUpdates() {
        if (locationApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationApiClient, this)
        }
    }

    /*********************************************
     * PermissionCallback
     */
    fun onPermissionResult(grantedMap: Map<String, Boolean>) {
        if (!locationApiClient.isConnected) {
            if (!locationApiClient.isConnecting) {
                locationApiClient.connect()
            }
            return
        }
        synchronized(requests) {
            if (!requests.isEmpty()) {
                try {
                    startLocationUpdates(requests[0])
                } catch (ex: SecurityException) {
                    rejectPendingRequests(ex)
                }

            }
        }
    }

    /*********************************************
     * GoogleApiClient.ConnectionCallbacks
     */
    override fun onConnectionSuspended(i: Int) {}

    override fun onConnected(bundle: Bundle?) {
        synchronized(requests) {
            if (!requests.isEmpty()) {
                try {
                    if (locationStatus === LocationServiceStatus.NOT_DETERMINED) {
                        requestLocationPermission()
                    } else {
                        startLocationUpdates(requests[0])
                    }
                } catch (ex: SecurityException) {
                    rejectPendingRequests(ex)
                }

            }
        }
    }


    /*********************************************
     * GoogleApiClient.OnConnectionFailedListener
     */
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        rejectPendingRequests(Exception(connectionResult.errorMessage))
    }

    /*********************************************
     * GoogleApiClient.OnConnectionFailedListener
     */
    override fun onLocationChanged(loc: android.location.Location) {
        synchronized(requests) {
            val location = lastLocation ?: return
            val validRequests = requests.filter { it.isLocationValid(location) }

            for (request in validRequests) {
                requests.remove(request)
                request.timer?.cancel()
                request.fulfill(location)
            }

            if (requests.isEmpty()) {
                stopLocationUpdates()
            }
        }
    }

    companion object {
        private val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private val DEFAULT_MIN_ACCURACY = 1000
        private val DEFAULT_MAX_AGE = 60
        private val DEFAULT_TIMEOUT = 30
    }
}

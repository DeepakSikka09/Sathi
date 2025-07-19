//package `in`.ecomexpress.sathi.utils
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import androidx.core.app.ActivityCompat
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationListener
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import `in`.ecomexpress.sathi.SathiApplication
//import `in`.ecomexpress.sathi.repo.IDataManager
//import `in`.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity
//import java.io.PrintWriter
//import java.io.StringWriter
//
//object SathiLocationUtility {
//    val TAG: String = SathiLocationUtility::class.java.simpleName
////    private const val SECONDS_15: Long = (1000 * 15).toLong()
//    private const val SECONDS_10: Long = (1000 * 10).toLong()
//    var mDataManager: IDataManager? = null
//    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
//
//    fun startLocationUpdates(iDataManager: IDataManager) {
//        mDataManager = iDataManager
//        val timestamp = System.currentTimeMillis()
//        if (ActivityCompat.checkSelfPermission(SathiApplication.getInstance(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                SathiApplication.getInstance(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            writeCrashes(timestamp,null)
//            return
//        }
//        val mLocationRequest= LocationRequest.Builder(
//            Priority.PRIORITY_HIGH_ACCURACY, SECONDS_10
//        ).build()
//
//        fusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(SathiApplication.getInstance())
//        try {
//            fusedLocationProviderClient?.requestLocationUpdates(
//                mLocationRequest,
//                mLocationCallback,
//                null
//            )
//            fusedLocationProviderClient?.lastLocation
//                ?.addOnSuccessListener { location: Location? ->
//                    if (location != null && location.latitude > 0 && location.longitude > 0) {
//                        mDataManager?.currentLatitude = location.latitude
//                        mDataManager?.currentLongitude = location.longitude
//                        Constants.CURRENT_LATITUDE =
//                            location.latitude.toString()
//                        Constants.CURRENT_LONGITUDE =
//                            location.longitude.toString()
//                        if (feInDCZone(
//                                location.latitude,
//                                location.longitude,
//                                mDataManager?.getDCLatitude() ?: 0.0,
//                                mDataManager?.getDCLongitude() ?: 0.0
//                            )
//                        ) {
//                            showPopupInDashBoard(SathiApplication.getInstance())
//                        }
//                    }
//                }
//        } catch (e: Exception) {
//            writeCrashes(timestamp, e)
//            Logger.e(TAG, e.toString())
//        }
//    }
//
//
//    private fun showPopupInDashBoard(context: Context) {
//        if (mDataManager?.feReachedDC != true) {
//            val intent = Intent(DashboardActivity.SHOW_POPUP_STOP_TRIP)
//            intent.putExtra(DashboardActivity.MESSAGE, DashboardActivity.SHOW_POPUP_STOP_TRIP)
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//        }
//    }
//    private fun feInDCZone(
//        latitude: Double,
//        longitude: Double,
//        dcLatitude: Double,
//        dcLongitude: Double
//    ): Boolean {
//        if (mDataManager?.tripId.equals("0", ignoreCase = true)) {
//            return false
//        }
//        val locationA = Location("point A")
//        locationA.latitude = latitude
//        locationA.longitude = longitude
//        val locationB = Location("point B")
//        locationB.latitude = dcLatitude
//        locationB.longitude = dcLongitude
//        val distance = locationA.distanceTo(locationB)
//        return distance <= Constants.LOCATION_ACCURACY
//    }
//
//
//    private fun writeCrashes(timeStamp: Long, e: Exception?) {
//        AppLogJsonProcessor.appendErrorJSONObject(
//            AppLogJsonProcessor.LogType.ERROR,
//            if(e!=null){
//                getExceptionAsString(e)
//            }else{
//                "Location Permission disabled."
//            },
//            mDataManager?.currentLatitude ?: 0.0,
//            mDataManager?.currentLongitude ?: 0.0, timeStamp,
//            mDataManager?.code
//        )
//    }
//
//    private fun getExceptionAsString(e: Exception): String {
//        val sw = StringWriter()
//        e.printStackTrace(PrintWriter(sw))
//        return sw.toString()
//    }
//
//    private val mLocationCallback: LocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            for (location in locationResult.locations) {
//                if (location.accuracy < Constants.LOCATION_ACCURACY) {
//                    if (location.latitude > 0 && location.longitude > 0) {
//                        SathiApplication.setGlobalFELat(location.latitude)
//                        SathiApplication.setGlobalFELng(location.longitude)
//                        mDataManager?.currentLatitude = location.latitude
//                        mDataManager?.currentLongitude = location.longitude
//                        Constants.CURRENT_LATITUDE = location.latitude.toString()
//                        Constants.CURRENT_LONGITUDE = location.longitude.toString()
//                    }
//                }
//            }
//        }
//    }
//
//    fun stopLocationUpdates() {
//        try {
//            fusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
//        } catch (e: java.lang.Exception) {
//            Logger.e(TAG, e.toString())
//        }
//    }
//
////    override fun onLocationChanged(location: Location) {
////        if (location!=null) {
////            Constants.CURRENT_LATITUDE = location.latitude.toString()
////            Constants.CURRENT_LONGITUDE = location.longitude.toString()
////        }
////    }
//
//}
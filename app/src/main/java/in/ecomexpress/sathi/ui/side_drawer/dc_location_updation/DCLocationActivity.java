package in.ecomexpress.sathi.ui.side_drawer.dc_location_updation;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityDcLocationBinding;
import in.ecomexpress.sathi.ui.base.BaseActivity;

@AndroidEntryPoint
public class DCLocationActivity extends BaseActivity<ActivityDcLocationBinding, DCLocationViewModel> implements IDCLocationNavigator, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = DCLocationActivity.class.getSimpleName();
    @Inject
    DCLocationViewModel mDCLocationViewModel;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000;
    private GoogleApiClient googleApiClient;
    private ActivityDcLocationBinding activityDcLocationBinding;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DCLocationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDCLocationViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, this);
        this.activityDcLocationBinding = getViewDataBinding();
        mDCLocationViewModel.context = this;
        LocationTracker.getInstance(getApplicationContext(), DCLocationActivity.this, true, false).runLocationServices();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        activityDcLocationBinding.headerText.awb.setText(R.string.update_sc_location);
        activityDcLocationBinding.headerText.backArrow.setOnClickListener(v -> onBackClick());
    }

    public void onBackClick() {
        finish();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public DCLocationViewModel getViewModel() {
        return mDCLocationViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dc_location;
    }

    @Override
    public void onBackPress() {
        onBackPressed();
    }

    @Override
    public void showSnackBar(String msg, boolean isSuccessMessage) {
        if(isSuccessMessage){
            showSuccessInfo(msg);
        } else{
            showSnackbar(msg);
        }
    }

    @Override
    public boolean latLongStatus() {
        if (activityDcLocationBinding == null) {
            return true;
        }

        String lat = activityDcLocationBinding.dcLat.getText().toString().trim();
        String lng = activityDcLocationBinding.dcLong.getText().toString().trim();
        return lat.isEmpty() || lng.isEmpty() || lat.equals("0.0") || lng.equals("0.0");
    }


    @Override
    public void showToastMsg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCurrentDCAddress(String address) {
        activityDcLocationBinding.dcAddress.setText(address);
    }

    @Override
    public void onLocationClick() {
        if (googleApiClient == null) {
            showSnackbar(getString(R.string.unable_to_connect_google_api_client_try_again));
        } else {
            googleApiClient.connect();
            showToastMsg(getString(R.string.getting_your_current_location));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location.getAccuracy() <= mDCLocationViewModel.getDataManager().getLiveTrackingAccuracy()) {
            activityDcLocationBinding.dcLat.setText(String.valueOf(location.getLatitude()));
            activityDcLocationBinding.dcLong.setText(String.valueOf(location.getLongitude()));
            mDCLocationViewModel.getAddress(this, location.getLatitude(), location.getLongitude());
        } else {
            showSnackbar(getString(R.string.unable_to_fetch_accurate_location));
        }
        mDCLocationViewModel.dismissProgressDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mDCLocationViewModel.showProgressDialog(this, getString(R.string.getting_dc_location));
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
}
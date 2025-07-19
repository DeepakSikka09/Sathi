package in.ecomexpress.sathi.ui.dashboard.stoptrip;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getResizedBitmap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.geolocations.LocationBeans;
import in.ecomexpress.geolocations.LocationService;
import in.ecomexpress.geolocations.LocationTracker;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.utils.custom_view.LocationCapture;
import in.ecomexpress.sathi.databinding.NewStoptripActivityBinding;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.cameraView.CameraXActivity;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.DigitalCropImageHandler;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.MessageManager;

@AndroidEntryPoint
public class StopTripActivity extends BaseActivity<NewStoptripActivityBinding, StopTripViewModel> implements StopTripCallBack {

    private final String TAG = StopTripActivity.class.getSimpleName();

    long stopTripReading;

    public static int imageCaptureCount = 0;


    @Inject
    StopTripViewModel mRunIdViewModel;

    static NewStoptripActivityBinding activityNewStopTripBinding;
    ArrayList<LocationBeans> arr_all_geoLocation = new ArrayList<>();
    ArrayList<LocationBeans> arr_all_geoLocation_withSpeed = new ArrayList<>();
    float total_distance = 0;
    float total_distance_with_speed = 0;
    Location start_location, end_location;
    ProgressDialog progressDialog, progressDialogWithSpeed;
    int pushApiSize = 0;
    private LocationCapture mLocation;
    String vehicleFileName, vehicleImgId, vehicleImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRunIdViewModel.setNavigator(this);
        activityNewStopTripBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, this);
        activityNewStopTripBinding.setViewModel(mRunIdViewModel);
        mRunIdViewModel.setNavigator(this);
        activityNewStopTripBinding.header.headingName.setText(R.string.stop_trip);

        mLocation = new LocationCapture(this);
        mLocation.setBlurRadius(5000);
        if (!mLocation.hasLocationEnabled()) {
            if (!mRunIdViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                in.ecomexpress.geolocations.LocationTracker.getInstance(StopTripActivity.this, StopTripActivity.this, true, false).runLocationServices();
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("live_tracking"));
        try {
            activityNewStopTripBinding.vehicleType.setText(mRunIdViewModel.getDataManager().getVehicleType());
            activityNewStopTripBinding.typeofvehicle.setText(mRunIdViewModel.getDataManager().getTypeOfVehicle());
            visibility(!activityNewStopTripBinding.typeofvehicle.getText().toString().equalsIgnoreCase("Others") && !activityNewStopTripBinding.typeofvehicle.getText().toString().equalsIgnoreCase("Counter"));
            activityNewStopTripBinding.route.setText(mRunIdViewModel.getDataManager().getRouteName());
            activityNewStopTripBinding.trip.setText(String.valueOf(mRunIdViewModel.getDataManager().getStartTripMeterReading()));
            mRunIdViewModel.getSizeOfPushApi();
            mRunIdViewModel.getLatLongForStopTrip();
        } catch (Exception e) {
            onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        activityNewStopTripBinding.header.backArrow.setOnClickListener(v -> finish());


        activityNewStopTripBinding.imageDelete.setOnClickListener(v -> {
            activityNewStopTripBinding.imageDelete.setVisibility(View.GONE);
            activityNewStopTripBinding.startImg.setVisibility(View.VISIBLE);
            activityNewStopTripBinding.startImg.setImageDrawable(getDrawable(R.drawable.cam_new));
            activityNewStopTripBinding.imageViewStart.setVisibility(View.GONE);
            vehicleFileName = null;
        });
        setObservers();
    }

    private void setObservers() {
        mRunIdViewModel.getDistanceCalculationWithSpeedApiResponseMutableLiveData().observe(this,distanceApiResponse -> {
            if (distanceApiResponse != null) {
                // modify the distance variable with speed
            if (distanceApiResponse.getDistances() !=null) {
                total_distance_with_speed = total_distance_with_speed+distanceApiResponse.getDistances().get(0).get(1).floatValue();
            }
            }
        });
        mRunIdViewModel.getDistanceCalculationApiResponseMutableLiveData().observe(this,distanceApiResponse -> {
            if (distanceApiResponse != null) {
                // modify the distance variable without speed
            if (distanceApiResponse.getDistances()!=null) {
                total_distance = total_distance+distanceApiResponse.getDistances().get(0).get(1).floatValue();
            }
            }
        });
    }


    private void visibility(boolean visible) {
        if (visible) {
            activityNewStopTripBinding.vehicleNumber.setVisibility(View.VISIBLE);
            if (mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                activityNewStopTripBinding.startTripReading.setVisibility(View.GONE);
                activityNewStopTripBinding.viclenumberTv.setVisibility(View.GONE);
                activityNewStopTripBinding.etMeter.setVisibility(View.GONE);
                activityNewStopTripBinding.tvOtherExpense.setVisibility(View.GONE);
                activityNewStopTripBinding.etOtherExprense.setVisibility(View.GONE);
                activityNewStopTripBinding.imageCaptureCard.setVisibility(View.GONE);
                activityNewStopTripBinding.selectDestination.setVisibility(View.GONE);
            } else {
                activityNewStopTripBinding.startTripReading.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.viclenumberTv.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.etMeter.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.tvOtherExpense.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.etOtherExprense.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.imageCaptureCard.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.selectDestination.setVisibility(View.VISIBLE);
            }
        } else {
            activityNewStopTripBinding.vehicleNumber.setVisibility(View.GONE);
            activityNewStopTripBinding.selectDestination.setVisibility(View.GONE);
            activityNewStopTripBinding.startTripReading.setVisibility(View.GONE);
            activityNewStopTripBinding.viclenumberTv.setVisibility(View.GONE);
            activityNewStopTripBinding.etMeter.setVisibility(View.GONE);
            activityNewStopTripBinding.tvOtherExpense.setVisibility(View.GONE);
            activityNewStopTripBinding.etOtherExprense.setVisibility(View.GONE);
            activityNewStopTripBinding.imageCaptureCard.setVisibility(View.GONE);
        }
    }


    @Override
    public StopTripViewModel getViewModel() {
        return mRunIdViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.new_stoptrip_activity;
    }


    @Override
    public void dismissDialog() {
        finish();

    }

    @Override
    public void StopTrip() {
        mRunIdViewModel.getUnSyncData();
    }

    @Override
    public void cancel() {

    }


    @Override
    public void CameraLaunch() {
        Constants.IsStartTrip = false;
        try {

            Intent i = new Intent(this, CameraXActivity.class);
            i.putExtra(Constants.CAMERA_TYPE, "stop_meter_reading");
            i.putExtra("emp_code", mRunIdViewModel.getDataManager().getEmp_code());
            startActivityForResult(i, 100245);
            applyTransitionToOpenActivity(this);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    public void onHandleError(String errorResponse) {

        try {
            runOnUiThread(() -> MessageManager.showToast(this, errorResponse));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    public void doLogout(String message) {
        try {
            runOnUiThread(() -> {
                showToast(getString(R.string.session_expire));
                mRunIdViewModel.clearAppData();
            });
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    public void showErrorMessage(boolean status) {

        runOnUiThread(() -> {
            if (status) {
                MessageManager.showToast(this, getResources().getString(R.string.http_500_msg));
            } else {
                MessageManager.showToast(this, getResources().getString(R.string.server_down_msg));
            }
        });
    }

    @Override
    public void getSize(int size) {
        try {
            if (size != 0) {
                mRunIdViewModel.showAlertForPendingSync();
            } else {
                LocationTracker.setStropLiveTrackingRunInfo("stop");
                stopService(new Intent(this, LocationService.class));
                stopTripData();
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void stopTripData() {
        try {
            if (activityNewStopTripBinding.typeofvehicle.getText().toString().equalsIgnoreCase("Others") || activityNewStopTripBinding.typeofvehicle.getText().toString().equalsIgnoreCase("Counter")) {
                if (isNetworkConnected()) {
                    LocationTracker.setStropLiveTrackingRunInfo("stop");
                    mRunIdViewModel.uploadData(this, "", -1, 0L, 0);
                    mRunIdViewModel.setFEReachedDC(true);
                } else {
                    mRunIdViewModel.is_start_clicked = true;
                    onHandleError(getString(R.string.no_network_error));
                }
            } else {
                if (mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                    mRunIdViewModel.saveStopTripDate();
                    mRunIdViewModel.is_start_clicked = true;
                    LocationTracker.setStropLiveTrackingRunInfo("stop");
                    mRunIdViewModel.uploadData(this, "", -1, 0L, 0);
                    mRunIdViewModel.setFEReachedDC(true);
                } else {
                    try {
                        mRunIdViewModel.saveStopTripDate();
                        if (activityNewStopTripBinding.etMeter.getText().toString().isEmpty()) {
                            runOnUiThread(() -> {
                                mRunIdViewModel.is_start_clicked = true;
                                Toast.makeText(this, getString(R.string.please_fill_all_details), Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                        stopTripReading = Long.parseLong(activityNewStopTripBinding.etMeter.getText().toString());
                        long startTripReading = mRunIdViewModel.getDataManager().getStartTripMeterReading();
                        if (stopTripReading - startTripReading == 0 && pushApiSize > 0) {
                            showSnackbar("you cannot enter stop meter reading same as start meter reading.");
                            return;
                        }
                        if (stopTripReading < startTripReading) {
                            mRunIdViewModel.is_start_clicked = true;
                            showSnackbar("Stop trip meter reading cannot be less than start meter reading..");
                            return;
                        }
                        if (vehicleFileName == null) {
                            mRunIdViewModel.is_start_clicked = true;
                            showSnackbar(getResources().getString(R.string.please_capture_image));
                            return;
                        }
                        if (isNetworkConnected()) {
                            long readingDiff = (startTripReading + mRunIdViewModel.getStopMeterDiff());
                            if (readingDiff < stopTripReading) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog);
                                String message = "Start trip and stop trip difference can not be greater than " + mRunIdViewModel.getStopMeterDiff();
                                AlertDialog dialog = alertDialog.setMessage(message).setTitle("Alert").setPositiveButton("ok", (dialogInterface, i) -> {
                                    mRunIdViewModel.is_start_clicked = true;
                                    dialogInterface.dismiss();
                                }).create();
                                dialog.setCancelable(true);
                                dialog.show();
                            } else {
                                uploadS3Object();
                                mRunIdViewModel.setFEReachedDC(true);
                            }
                        } else {
                            onHandleError(getString(R.string.no_network_error));
                        }
                    } catch (Exception e) {
                        mRunIdViewModel.is_start_clicked = true;
                        Toast.makeText(this, "Please enter valid meter reading. Decimal not accepted.", Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    public void setStopMeterReading() {

        mRunIdViewModel.getDataManager().setEndTripKm(stopTripReading);
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void openEarnDialog(float delvrd_shpmnts, long earned_money) {

        cancel();
        // earnDPDialogListener.openDPEarnDialog(delvrd_shpmnts, earned_money);
    }

    @Override
    public void showHandleError(boolean status) {

        if (status) {
            showSnackbar(getString(R.string.http_500_msg));
        } else {
            showSnackbar(getString(R.string.server_down_msg));
        }
    }

    @Override
    public String getStopVehicleType() {

        return activityNewStopTripBinding.typeofvehicle.getText().toString();
    }

    @Override
    public void setPushApiSize(int pushApisize) {

        this.pushApiSize = pushApisize;
    }


    @Override
    public void disableSubmitButton() {
        this.runOnUiThread(() -> activityNewStopTripBinding.tvStart.setEnabled(false));
    }

    @Override
    public void enableSubmitButton() {
        this.runOnUiThread(() -> activityNewStopTripBinding.tvStart.setEnabled(true));
    }

    @Override
    public long getStopMeterReadingXML() {

        return Long.parseLong(activityNewStopTripBinding.etMeter.getText().toString());
    }


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivityContext()).unregisterReceiver(mMessageReceiver);
        mRunIdViewModel.getDistanceCalculationApiResponseMutableLiveData().removeObservers(this);
        mRunIdViewModel.getDistanceCalculationWithSpeedApiResponseMutableLiveData().removeObservers(this);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (resultCode == 2) {
                vehicleFileName = data.getStringExtra("fileName");
                vehicleImgId = data.getStringExtra("imgId");
                vehicleImgUri = data.getStringExtra("imgUri");

                setImageShow(data, activityNewStopTripBinding.imageViewStart);
                activityNewStopTripBinding.imageCaptureCard.setBackgroundColor(getResources().getColor(R.color.white));
                activityNewStopTripBinding.imageViewStart.setVisibility(View.VISIBLE);
                activityNewStopTripBinding.startImg.setVisibility(View.GONE);
                activityNewStopTripBinding.imageDelete.setVisibility(View.VISIBLE);
                enableSubmitButton();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setImageShow(Intent data, AppCompatImageView imageView) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(data.getStringExtra("URI")));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream); // 0=lowest, 100=highest quality
            byte[] byteArray = stream.toByteArray();
            Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap resizeBitmap = getResizedBitmap(yourCompressBitmap, 1200);
            imageView.setImageBitmap(resizeBitmap);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }


    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            uploadS3Object();
            mRunIdViewModel.setFEReachedDC(true);
        }
    };

    private void uploadS3Object() {
        try {
            calculateDistance();
        } catch (Exception e) {
            onHandleError(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistance() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(getActivityContext(),android.R.style.Theme_Material_Light_Dialog);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("Calculating distance, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mRunIdViewModel.getDataManager().setLiveTrackingCalculatedDistance(0);
                    arr_all_geoLocation.addAll(LocationTracker.getLatLng());
                    ArrayList<LocationBeans> latList = new ArrayList<>();
                    ArrayList<LocationBeans> filteredLatList = new ArrayList<>();
                    ArrayList<LocationBeans> filteredLatListWithSpeed = new ArrayList<>();
                    for (int i = 0; i < arr_all_geoLocation.size(); i++) {
                        try {
                            LocationBeans locationBeans = new LocationBeans();
                            locationBeans.setLatitude(arr_all_geoLocation.get(i).getLatitude());
                            locationBeans.setLongitude(arr_all_geoLocation.get(i).getLongitude());
                            locationBeans.setSpeed(arr_all_geoLocation.get(i).getSpeed());
                            locationBeans.setTimeStamp(arr_all_geoLocation.get(i).getTimeStamp());
                            latList.add(locationBeans);
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                    progressDialog.setMax(latList.size());
                    mRunIdViewModel.getDataManager().setLiveTrackingCount(latList.size());
                    float distance, distance_for_filter;
                    int count = 0;
                    double speed;
                    long time_diff, seconds;
                    for (int i = 1; i < latList.size(); i++) {
                        start_location = new Location("");
                        start_location.setLatitude(latList.get(count).getLatitude());
                        start_location.setLongitude(latList.get(count).getLongitude());
                        start_location.setTime(latList.get(count).getTimeStamp());
                        end_location = new Location("");
                        end_location.setLatitude(latList.get(i).getLatitude());
                        end_location.setLongitude(latList.get(i).getLongitude());
                        end_location.setTime(latList.get(i).getTimeStamp());
                        time_diff = (end_location.getTime() - start_location.getTime());
                        seconds = TimeUnit.MILLISECONDS.toSeconds(time_diff);
                        if (seconds > mRunIdViewModel.getDataManager().getLiveTrackingMINSpeed()) {
                            filteredLatList.add(latList.get(count));
                            count = i;
                        }
                    }
                    count = 0;
                    for (int i = 1; i < filteredLatList.size(); i++) {
                        start_location = new Location("");
                        start_location.setLatitude(filteredLatList.get(count).getLatitude());
                        start_location.setLongitude(filteredLatList.get(count).getLongitude());
                        start_location.setTime(filteredLatList.get(count).getTimeStamp());
                        end_location = new Location("");
                        end_location.setLatitude(filteredLatList.get(i).getLatitude());
                        end_location.setLongitude(filteredLatList.get(i).getLongitude());
                        end_location.setTime(filteredLatList.get(i).getTimeStamp());
                        distance_for_filter = start_location.distanceTo(end_location);
                        time_diff = (end_location.getTime() - start_location.getTime());
                        seconds = TimeUnit.MILLISECONDS.toSeconds(time_diff);
                        speed = distance_for_filter / seconds;
                        speed = speed * 3.6;
                        if (speed < mRunIdViewModel.getDataManager().getLiveTrackingSpeed()) {
                            filteredLatListWithSpeed.add(filteredLatList.get(count));
                            count = i;
                        }
                    }
                    for (int i = 1; i < filteredLatListWithSpeed.size(); i++) {
                        try {
                            start_location = new Location("");
                            start_location.setLatitude(filteredLatListWithSpeed.get(i - 1).getLatitude());
                            start_location.setLongitude(filteredLatListWithSpeed.get(i - 1).getLongitude());
                            start_location.setTime(filteredLatListWithSpeed.get(i - 1).getTimeStamp());
                            end_location = new Location("");
                            end_location.setLatitude(filteredLatListWithSpeed.get(i).getLatitude());
                            end_location.setLongitude(filteredLatListWithSpeed.get(i).getLongitude());
                            end_location.setTime(filteredLatListWithSpeed.get(i).getTimeStamp());
                            distance = start_location.distanceTo(end_location);
                            if (distance > 3500) {
                                try {
                                    LatLng lastlatLng = new LatLng(start_location.getLatitude(), start_location.getLongitude());
                                    LatLng firstlatLng = new LatLng(end_location.getLatitude(), end_location.getLongitude());
                                    if (mRunIdViewModel.getDataManager().getDistanceAPIEnabled()) {
                                        GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
                                    DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(firstlatLng).optimizeWaypoints(true).destination(lastlatLng).awaitIgnoreError();
                                    String dis = (result.routes[0].legs[0].distance.humanReadable);
                                    if (dis.endsWith("km")) {
                                        distance = Float.parseFloat(dis.replaceAll("[^\\d.]", "")) * 1000;
                                    } else {
                                        distance = Float.parseFloat(dis.replaceAll("[^\\d.]", ""));
                                    }
                                    total_distance = total_distance + distance;
                                    } else {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(start_location.getLongitude());
                                        builder.append(",");
                                        builder.append(start_location.getLatitude());
                                        builder.append(";");
                                        builder.append(end_location.getLongitude());
                                        builder.append(",");
                                        builder.append(end_location.getLatitude());
                                        mRunIdViewModel.distanceCalculationApi(builder.toString(), false);
                                    }
                                }catch (Exception e) {
                                    distance = start_location.distanceTo(end_location);
                                    total_distance = total_distance + distance;
                                }
                            } else {
                                total_distance = total_distance + distance;
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                            continue;
                        }
                        publishProgress(i);
                    }
                    mRunIdViewModel.getDataManager().setLiveTrackingCalculatedDistance(total_distance);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                calculateDistanceWithSpeed();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistanceWithSpeed() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialogWithSpeed = new ProgressDialog(getActivityContext(),android.R.style.Theme_Material_Light_Dialog);
                progressDialogWithSpeed.setIndeterminate(false);
                progressDialogWithSpeed.setMessage("Calculating distance, please wait...");
                progressDialogWithSpeed.setCancelable(false);
                progressDialogWithSpeed.show();
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                total_distance_with_speed = 0;
                mRunIdViewModel.getDataManager().setLiveTrackingCalculatedDistanceWithSpeed(0);
                arr_all_geoLocation_withSpeed.addAll(LocationTracker.getLatLng());
                ArrayList<LocationBeans> latList_speed = new ArrayList<>();
                ArrayList<LocationBeans> filteredLatListSpeed = new ArrayList<>();
                int count = 0;
                try {
                    for (int i = 0; i < arr_all_geoLocation_withSpeed.size(); i++) {
                        if (arr_all_geoLocation_withSpeed.get(i).getGps() != null && !arr_all_geoLocation_withSpeed.get(i).getGps().equalsIgnoreCase("0.0") && !arr_all_geoLocation.get(i).getGps().equalsIgnoreCase("null")) {
                            double speed = Double.parseDouble(arr_all_geoLocation_withSpeed.get(i).getGps()) * 3.6;
                            if (speed > mRunIdViewModel.getDataManager().getLiveTrackingMINSpeed() && speed < mRunIdViewModel.getDataManager().getLiveTrackingSpeed()) {
                                LocationBeans locationBeans = new LocationBeans();
                                locationBeans.setLatitude(arr_all_geoLocation_withSpeed.get(i).getLatitude());
                                locationBeans.setLongitude(arr_all_geoLocation_withSpeed.get(i).getLongitude());
                                locationBeans.setSpeed(arr_all_geoLocation_withSpeed.get(i).getGps());
                                locationBeans.setTimeStamp(arr_all_geoLocation_withSpeed.get(i).getTimeStamp());
                                latList_speed.add(locationBeans);
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                for (int i = 1; i < latList_speed.size(); i++) {
                    start_location = new Location("");
                    start_location.setLatitude(latList_speed.get(i - 1).getLatitude());
                    start_location.setLongitude(latList_speed.get(i - 1).getLongitude());
                    start_location.setTime(latList_speed.get(i - 1).getTimeStamp());
                    end_location = new Location("");
                    end_location.setLatitude(latList_speed.get(i).getLatitude());
                    end_location.setLongitude(latList_speed.get(i).getLongitude());
                    end_location.setTime(latList_speed.get(i).getTimeStamp());
                    long time_diff = (end_location.getTime() - start_location.getTime());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(time_diff);
                    if (seconds > mRunIdViewModel.getDataManager().getLiveTrackingMINSpeed()) {
                        filteredLatListSpeed.add(latList_speed.get(count));
                        count = i;
                    }
                }
                float distance;
                for (int i = 1; i < filteredLatListSpeed.size(); i++) {
                    try {
                        start_location = new Location("");
                        start_location.setLatitude(filteredLatListSpeed.get(i - 1).getLatitude());
                        start_location.setLongitude(filteredLatListSpeed.get(i - 1).getLongitude());
                        end_location = new Location("");
                        end_location.setLatitude(filteredLatListSpeed.get(i).getLatitude());
                        end_location.setLongitude(filteredLatListSpeed.get(i).getLongitude());
                        distance = start_location.distanceTo(end_location);
                        if (distance > mRunIdViewModel.getDataManager().getDistance()) {
                            if (mRunIdViewModel.getDataManager().getDistanceAPIEnabled()) {
                                LatLng lastlatLng = new LatLng(start_location.getLatitude(), start_location.getLongitude());
                                LatLng firstlatLng = new LatLng(end_location.getLatitude(), end_location.getLongitude());
                                GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
                                DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(firstlatLng).optimizeWaypoints(true).destination(lastlatLng).awaitIgnoreError();
                                String dis = (result.routes[0].legs[0].distance.humanReadable);
                            if (dis.endsWith("km")) {
                                distance = Float.parseFloat(dis.replaceAll("[^\\d.]", "")) * 1000;
                            } else {
                                distance = Float.parseFloat(dis.replaceAll("[^\\d.]", ""));
                            }
                                total_distance_with_speed = total_distance_with_speed + distance;
                            } else {
                                StringBuilder builder = new StringBuilder();
                                builder.append(start_location.getLongitude());
                                builder.append(",");
                                builder.append(start_location.getLatitude());
                                builder.append(";");
                                builder.append(end_location.getLongitude());
                                builder.append(",");
                                builder.append(end_location.getLatitude());
                                mRunIdViewModel.distanceCalculationApi(builder.toString(), true);
                            }
                        }
                    }catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                        i++;
                    }
                }
                mRunIdViewModel.getDataManager().setLiveTrackingCalculatedDistanceWithSpeed(total_distance_with_speed);
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialogWithSpeed.dismiss();
                float diff = getStopMeterReadingXML() - mRunIdViewModel.getDataManager().getStartTripMeterReading();
                float distanceTracking = LocationTracker.calculateDistanceLibrary(mRunIdViewModel.getDataManager().getLiveTrackingCalculatedDistance() / 1000, diff);
                runOnUiThread(() -> {
                    if (Constants.CURRENT_LATITUDE != null && Constants.CURRENT_LONGITUDE != null) {
                        mRunIdViewModel.uploadAWSImage(StopTripActivity.this, vehicleImgUri, vehicleFileName, Long.parseLong(activityNewStopTripBinding.etMeter.getText().toString()), vehicleImgId, distanceTracking);
                    } else {
                        mRunIdViewModel.uploadAWSImage(StopTripActivity.this, vehicleImgUri, vehicleFileName, Long.parseLong(activityNewStopTripBinding.etMeter.getText().toString()), vehicleImgId, distanceTracking);
                    }
                });
            }
        }.execute();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(StopTripActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

}
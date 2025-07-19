package in.ecomexpress.sathi.ui.dashboard.starttrip;


import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;
import static in.ecomexpress.sathi.utils.CommonUtils.verifyVehicleNumber;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getRealPathFromURI;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getResizedBitmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityNewStartTripBinding;
import in.ecomexpress.sathi.repo.remote.model.trip.ImageResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.DigitalCropImageHandler;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.MessageManager;
import in.ecomexpress.sathi.utils.cameraView.CameraXActivity;
import in.ecomexpress.sathi.utils.custom_view.LocationCapture;

@AndroidEntryPoint
public class StartTripActivity extends BaseActivity<ActivityNewStartTripBinding, StartTripViewModel> implements StartTripCallBack {

    private final String TAG = StartTripActivity.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    public static int imageCaptureCount = 0;
    private LocationCapture mLocation;

    @SuppressLint("StaticFieldLeak")
    static ImageHandler imageHandler;
    String vehicleType = "", typeOfVehicle = "";
    String getVehicle = null;
    ArrayList<ImageResponse> imageResponseArrayList = new ArrayList<>();

    @Inject
    StartTripViewModel mRunIdViewModel;

    ActivityNewStartTripBinding activityNewStartTripBinding;
    ArrayAdapter<CharSequence> TypeOfVehicleAdapter;
    long timestamp;
    String vehicleFileName, vehicleImgId, vehicleImgUri;
    String selfieFileName, selfieImgId, selfieImgUri;
    private String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    private String device_name = "android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRunIdViewModel.setNavigator(this);
        context = this;
        logScreenNameInGoogleAnalytics(TAG, this);
        activityNewStartTripBinding = getViewDataBinding();
        mRunIdViewModel.setNavigator(this);
        mRunIdViewModel.getClickStart();
        activityNewStartTripBinding.header.headingName.setText(R.string.start_trip);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mLocation = new LocationCapture(this);
        mLocation.setBlurRadius(5000);
        if (!mLocation.hasLocationEnabled()) {
            if (!mRunIdViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                in.ecomexpress.geolocations.LocationTracker.getInstance(StartTripActivity.this, StartTripActivity.this, true, false).runLocationServices();
            }
        }

        try {
            if (mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Ecom express") /*|| mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Ecom Vehicle")*/)
                activityNewStartTripBinding.spinnerVehicleType.setSelection(1);
            if (mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("0") || mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Self") /*|| mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Own Vehicle")*/) {
                if (mRunIdViewModel.getDataManager().isDCLocationUpdateAllowed()) {
                    TypeOfVehicleAdapter = ArrayAdapter.createFromResource(this
                            , R.array.counter_veh_type, android.R.layout.simple_spinner_item);
                } else {
                    TypeOfVehicleAdapter = ArrayAdapter.createFromResource(this, R.array.type_of_veh, android.R.layout.simple_spinner_item);
                }
            } else if (mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Ecom express")) {
                TypeOfVehicleAdapter = ArrayAdapter.createFromResource(this, R.array.type_of_veh1, android.R.layout.simple_spinner_item);
            }
            TypeOfVehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            activityNewStartTripBinding.spinnerTypeofvehicle.setAdapter(TypeOfVehicleAdapter);

            setSavedSpinnerValues();
            mRunIdViewModel.onVehicleNumber();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }

        activityNewStartTripBinding.etRoute.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String str = activityNewStartTripBinding.etRoute.getText().toString();
                if (str == null) {
                    activityNewStartTripBinding.etRoute.setHint(getString(R.string.hint_enter_veh));
                } else {
                    str = str.toUpperCase();
                    activityNewStartTripBinding.etRoute.setText(str);
                }
            } else {
                activityNewStartTripBinding.etRoute.setHint("");
            }
        });


        activityNewStartTripBinding.imageDelete.setOnClickListener(v -> {
            activityNewStartTripBinding.imageDelete.setVisibility(View.GONE);
            activityNewStartTripBinding.startImg.setVisibility(View.VISIBLE);
            activityNewStartTripBinding.startImg.setImageDrawable(getDrawable(R.drawable.cam_new));
            activityNewStartTripBinding.imageViewStart.setVisibility(View.GONE);
            //activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.VISIBLE);
            vehicleFileName = null;
            if (!imageResponseArrayList.isEmpty()) {
                imageResponseArrayList.remove(0);
            }
        });

        activityNewStartTripBinding.imageDelete2.setOnClickListener(v -> {
            activityNewStartTripBinding.imageDelete2.setVisibility(View.GONE);
            activityNewStartTripBinding.tvStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_disable_color)));
            //activityNewStartTripBinding.tvStart.setEnabled(false);
            activityNewStartTripBinding.imageCamera2.setVisibility(View.VISIBLE);
            activityNewStartTripBinding.imageCamera2.setImageDrawable(getDrawable(R.drawable.cam_new));
            activityNewStartTripBinding.imageViewStartHelmet.setVisibility(View.GONE);
            selfieFileName = null;
            if (imageResponseArrayList.size() > 1) {
                imageResponseArrayList.remove(1);
            } else if (!imageResponseArrayList.isEmpty()) {
                imageResponseArrayList.remove(0);
            }
        });

        activityNewStartTripBinding.header.backArrow.setOnClickListener(v -> finish());

    }


    private void setSavedSpinnerValues() {
        try {
            if (!mRunIdViewModel.getDataManager().getTypeOfVehicle().equalsIgnoreCase("0")) {
                if (mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Self")) {
                    String[] vehicleTypeArr;
                    if (mRunIdViewModel.getDataManager().isDCLocationUpdateAllowed()) {
                        vehicleTypeArr = getResources().getStringArray(R.array.counter_veh_type);
                    } else {
                        vehicleTypeArr = getResources().getStringArray(R.array.type_of_veh);
                    }
                    for (int i = 0; i < vehicleTypeArr.length; i++) {
                        if (vehicleTypeArr[i].equalsIgnoreCase(mRunIdViewModel.getDataManager().getTypeOfVehicle())) {
                            activityNewStartTripBinding.spinnerTypeofvehicle.setSelection(i, false);
                            break;
                        }
                    }
                } else if (mRunIdViewModel.getDataManager().getVehicleType().equalsIgnoreCase("Ecom express")) {
                    String[] vehicleTypeArr = getResources().getStringArray(R.array.type_of_veh1);
                    for (int i = 0; i < vehicleTypeArr.length; i++) {
                        if (vehicleTypeArr[i].equalsIgnoreCase(mRunIdViewModel.getDataManager().getTypeOfVehicle())) {
                            activityNewStartTripBinding.spinnerTypeofvehicle.setSelection(i, false);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public StartTripViewModel getViewModel() {

        return mRunIdViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_start_trip;
    }


    @Override
    public void dismissDialog() {

    }

    @Override
    public void startTripSyncDrs() {

        try {
            Intent intent = new Intent("StartTrip");
            intent.putExtra("message", "start");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            mRunIdViewModel.onDRSListApiCall();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }

    }

    @Override
    public void StartTrip() {

        try {
            if (typeOfVehicle.equalsIgnoreCase("4 Wheeler")) {
                mRunIdViewModel.getDataManager().setMAP_DRIVING_MODE("d");
            } else {
                mRunIdViewModel.getDataManager().setMAP_DRIVING_MODE("bicycling");
            }
            if (typeOfVehicle.equalsIgnoreCase("Others") || typeOfVehicle.equalsIgnoreCase("Counter")) {
                mRunIdViewModel.uploadData(this, "", "", -1, "", vehicleType, typeOfVehicle, "", 0L, imageResponseArrayList, device_name);
            } else {
                if (mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                    if (activityNewStartTripBinding.etRoute.getText().toString().isEmpty()) {
                        showInfo(getResources().getString(R.string.please_fill_all_details));
                        mRunIdViewModel.is_start_clicked = true;
                        return;
                    }
                    String vehicleNo = activityNewStartTripBinding.etRoute.getText().toString().trim();
                    if (verifyVehicleNumber(vehicleNo)) {
                        showInfo(getResources().getString(R.string.vehicle_number_validation));
                    } else {
                        mRunIdViewModel.uploadData(this, "", "", -1, vehicleNo, vehicleType, typeOfVehicle, vehicleNo, 0L, imageResponseArrayList, device_name);
                    }
                    mRunIdViewModel.is_start_clicked = true;
                } else {
                    try {
                        if (activityNewStartTripBinding.etRoute.getText().toString().isEmpty() || activityNewStartTripBinding.etMeter.getText().toString().isEmpty()) {
                            showInfo(getResources().getString(R.string.please_fill_all_details));
                            mRunIdViewModel.is_start_clicked = true;
                            return;
                        }
                        String vehicleNo = activityNewStartTripBinding.etRoute.getText().toString().trim();
                        if (verifyVehicleNumber(vehicleNo)) {
                            showInfo(getResources().getString(R.string.vehicle_number_validation));
                            mRunIdViewModel.is_start_clicked = true;
                            return;
                        }
                        if (Integer.parseInt(activityNewStartTripBinding.etMeter.getText().toString()) >= 999999) {
                            showInfo(getString(R.string.start_trip_reading_should_be_less_than_999999));
                            mRunIdViewModel.is_start_clicked = true;
                            return;
                        }
                        if (vehicleFileName == null) {
                            showInfo(getResources().getString(R.string.please_capture_image));
                            mRunIdViewModel.is_start_clicked = true;
                            return;
                        }
                        if (typeOfVehicle.equalsIgnoreCase("3 Wheeler") || typeOfVehicle.equalsIgnoreCase("4 Wheeler")) {
                            device_name = "android";
                        } else if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                            device_name = "Newland";
                        } else {
                            device_name = "android";
                            if (selfieFileName == null) {
                                showInfo(getResources().getString(R.string.please_capture_image));
                                mRunIdViewModel.is_start_clicked = true;
                                return;
                            }
                        }

                        if (Float.parseFloat(activityNewStartTripBinding.etMeter.getText().toString()) > 0) {
                            float reading = Float.parseFloat(activityNewStartTripBinding.etMeter.getText().toString());
                            if (getVehicle != null && getVehicle.equalsIgnoreCase(activityNewStartTripBinding.etRoute.getText().toString())) {
                                try {
                                    long getStopReading = mRunIdViewModel.getDataManager().getEndTripKm();
                                    if (reading < getStopReading || reading == getStopReading) {
                                        mRunIdViewModel.is_start_clicked = true;
                                        Toast.makeText(context, "Start trip reading should be greater than " + getStopReading + " km.", Toast.LENGTH_SHORT).show();
                                    } else if (reading > mRunIdViewModel.startMeterReadingShouldBe() && mRunIdViewModel.startMeterReadingShouldBe() > 0) {
                                        mRunIdViewModel.showAlertDialog(context, mRunIdViewModel.startMeterReadingShouldBe() - mRunIdViewModel.getDataManager().getEndTripKm());
                                        mRunIdViewModel.is_start_clicked = true;
                                    } else {
                                        if (isNetworkConnected()) {
                                            mRunIdViewModel.uploadData(this, vehicleImgUri, "", -1, vehicleNo, vehicleType, typeOfVehicle, vehicleNo, Long.parseLong(activityNewStartTripBinding.etMeter.getText().toString()), imageResponseArrayList, device_name);
                                        } else {
                                            mRunIdViewModel.is_start_clicked = true;
                                            onHandleError(getString(R.string.no_network_error));
                                        }
                                    }
                                } catch (Exception e) {
                                    Logger.e(TAG, String.valueOf(e));
                                }
                            } else {
                                if (isNetworkConnected()) {
                                    mRunIdViewModel.uploadData(this, vehicleImgUri, "", -1, vehicleNo, vehicleType, typeOfVehicle, vehicleNo, Long.parseLong(activityNewStartTripBinding.etMeter.getText().toString()), imageResponseArrayList, device_name);
                                } else {
                                    mRunIdViewModel.is_start_clicked = true;
                                    onHandleError(getString(R.string.no_network_error));
                                }
                            }
                        } else {
                            mRunIdViewModel.is_start_clicked = true;
                            onHandleError("Please enter valid odometer reading.");
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter valid meter reading.Decimal not accepted.", Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            }
        } catch (
                Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }

    }


    @Override
    public void CameraLaunch() {

        timestamp = Calendar.getInstance().getTimeInMillis();
        try {
            Constants.IsStartTrip = true;
            Intent i = new Intent(this, CameraXActivity.class);
            i.putExtra(Constants.CAMERA_TYPE, "meter_reading");
            i.putExtra("emp_code", mRunIdViewModel.getDataManager().getEmp_code());
            startActivityForResult(i, 100245);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    public void VehicleType(String vehicleType) {
        try {
            this.vehicleType = vehicleType;
            ArrayAdapter<CharSequence> adapter;
            if (mRunIdViewModel.getDataManager().isDCLocationUpdateAllowed()) {
                adapter = ArrayAdapter.createFromResource(this, vehicleType.equalsIgnoreCase("Ecom express") ? R.array.type_of_veh1 : R.array.counter_veh_type, android.R.layout.simple_spinner_item);
            } else {
                adapter = ArrayAdapter.createFromResource(this, vehicleType.equalsIgnoreCase("Ecom express") ? R.array.type_of_veh1 : R.array.type_of_veh, android.R.layout.simple_spinner_item);
            }
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            activityNewStartTripBinding.spinnerTypeofvehicle.setAdapter(adapter);
            if (vehicleType.equalsIgnoreCase(mRunIdViewModel.getDataManager().getVehicleType())) {
                setSavedSpinnerValues();
            }
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onHandleError(String errorResponse) {

        try {
            context.runOnUiThread(() -> MessageManager.showToast(context, errorResponse));
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void TypeOfVehicle(String value) {

        typeOfVehicle = value;
        if (value.equalsIgnoreCase("Others") || value.equalsIgnoreCase("Counter")) {
            mRunIdViewModel.getDataManager().setCounterDelivery(value.equalsIgnoreCase("Counter"));
            activityNewStartTripBinding.etRoute.setVisibility(View.GONE);
            activityNewStartTripBinding.etMeter.setVisibility(View.GONE);
            activityNewStartTripBinding.viclemternumerTv.setVisibility(View.GONE);
            activityNewStartTripBinding.viclenumberTv.setVisibility(View.GONE);
            activityNewStartTripBinding.imageCaptureCard.setVisibility(View.GONE);
            activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.GONE);
            buttonEnable();
        } else {
            mRunIdViewModel.getDataManager().setCounterDelivery(false);
            if (mRunIdViewModel.getDataManager().getIsAdmEmp()) {
                activityNewStartTripBinding.etMeter.setVisibility(View.GONE);
                activityNewStartTripBinding.viclemternumerTv.setVisibility(View.GONE);
                activityNewStartTripBinding.imageCaptureCard.setVisibility(View.GONE);
                activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.GONE);
            } else {
                activityNewStartTripBinding.etMeter.setVisibility(View.VISIBLE);
                activityNewStartTripBinding.viclemternumerTv.setVisibility(View.VISIBLE);
                activityNewStartTripBinding.imageCaptureCard.setVisibility(View.VISIBLE);
                if (value.equalsIgnoreCase("3 Wheeler") || value.equalsIgnoreCase("4 Wheeler") || device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                    activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.GONE);
                } else {
                    activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.VISIBLE);
                }
            }
            activityNewStartTripBinding.etRoute.setVisibility(View.VISIBLE);
            activityNewStartTripBinding.viclenumberTv.setVisibility(View.VISIBLE);
        }

        hideKeyboard(StartTripActivity.this);
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
    public void closeDialogopenDrs() {
        startActivity(ToDoListActivity.getStartIntent(StartTripActivity.this).putExtra("From", "startTrip"));
        finish();
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showDescription(String description) {

        context.runOnUiThread(() -> Toast.makeText(context, description, Toast.LENGTH_LONG).show());
    }

    @Override
    public void sendVehicleNumber(String routeName) {

        if (routeName == null) {

        } else {
            getVehicle = routeName;
            activityNewStartTripBinding.etRoute.setText(routeName);
            activityNewStartTripBinding.etRoute.setSelection(activityNewStartTripBinding.etRoute.getText().length());
        }
    }

    @Override
    public void showErrorMessage(boolean status) {

        context.runOnUiThread(() -> {
            if (status) {
                MessageManager.showToast(context, getResources().getString(R.string.http_500_msg));
            } else {
                MessageManager.showToast(context, getResources().getString(R.string.server_down_msg));
            }
        });
    }

    @Override
    public Context getActivityContext() {
        return StartTripActivity.this;
    }

    @Override
    public void showError(String error) {

        try {
            showSnackbar(error);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void disableSubmitButton() {

        runOnUiThread(() -> activityNewStartTripBinding.tvStart.setEnabled(false));
    }

    @Override
    public void enableSubmitButton() {

        try {
            new Handler(Objects.requireNonNull(Looper.myLooper())).post(() -> activityNewStartTripBinding.tvStart.setEnabled(true));
        } catch (Exception e) {
            showDescription(e.getMessage());
        }

    }

    @Override
    public void openDPDailyEarningDialog(float potential_earning, long fwd_shpmnt_number) {

    }

    @Override
    public void FrontCameraLaunch() {
        timestamp = Calendar.getInstance().getTimeInMillis();
        try {
            Constants.IsStartTrip = true;
            Intent i = new Intent(this, CameraXActivity.class);
            i.putExtra(Constants.CAMERA_TYPE, "selfie_helmet");
            i.putExtra("emp_code", mRunIdViewModel.getDataManager().getEmp_code());
            startActivityForResult(i, 100245);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {


            if (resultCode == 2) {

                vehicleFileName = data.getStringExtra("fileName");
                vehicleImgId = data.getStringExtra("imgId");
                vehicleImgUri = data.getStringExtra("imgUri");

                ImageResponse imageResponse = new ImageResponse(vehicleFileName, Integer.parseInt(vehicleImgId));
                imageResponseArrayList.add(imageResponse);

                setImageShow(data, activityNewStartTripBinding.imageViewStart);
                activityNewStartTripBinding.imageCaptureCard.setBackgroundColor(getResources().getColor(R.color.white));
                //activityNewStartTripBinding.imageCaptureCardHelmet.setVisibility(View.VISIBLE);
                activityNewStartTripBinding.imageViewStart.setVisibility(View.VISIBLE);
                activityNewStartTripBinding.startImg.setVisibility(View.GONE);
                activityNewStartTripBinding.imageDelete.setVisibility(View.VISIBLE);

            } else if (resultCode == 3) {

                selfieFileName = data.getStringExtra("fileName");
                selfieImgId = data.getStringExtra("imgId");
                selfieImgUri = data.getStringExtra("imgUri");
                ImageResponse imageResponse = new ImageResponse(selfieFileName, Integer.parseInt(selfieImgId));
                imageResponseArrayList.add(imageResponse);
                activityNewStartTripBinding.imageViewStartHelmet.setVisibility(View.VISIBLE);
                setImageShow(data, activityNewStartTripBinding.imageViewStartHelmet);
                buttonEnable();
                activityNewStartTripBinding.imageCaptureCardHelmet.setBackgroundColor(getResources().getColor(R.color.white));
                activityNewStartTripBinding.imageCamera2.setVisibility(View.GONE);
                activityNewStartTripBinding.imageDelete2.setVisibility(View.VISIBLE);

            } else {
                if (Constants.IsStartTrip) {
                    String filename = mRunIdViewModel.getEmpCode() + "_" + "_startTrip.png";
                    File fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    File cacheFile = new File(fileDir, filename);
                    String filePath = getRealPathFromURI(this, cacheFile.getAbsolutePath());
                    CryptoUtils.encryptFile(filePath, filePath, Constants.ENC_DEC_KEY);
                } else {
                    imageHandler.onActivityResult(requestCode, resultCode, data);
                }
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {
                    if (result.getContents() == null) {
                        Logger.e(TAG, TAG);
                    } else {
                        try {
                            mRunIdViewModel.DPScanReferenceCode(result.getContents());
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        super.onActivityResult(requestCode, resultCode, data);
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


    public String saveFile(Bitmap bitmap) {
        String filename = "trip.png";
        String path = "";
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, filename);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            path = dest.getPath();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return path;
    }

    @Override
    public void showSuccess(String msg) {
        showSuccessInfo(msg);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(StartTripActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void buttonEnable() {
        activityNewStartTripBinding.tvStart.setEnabled(true);
        activityNewStartTripBinding.tvStart.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_ecom)));
    }

}
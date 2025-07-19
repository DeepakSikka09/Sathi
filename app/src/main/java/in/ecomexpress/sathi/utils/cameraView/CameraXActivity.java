package in.ecomexpress.sathi.utils.cameraView;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.WatermarkUtils.applyWaterMark;
import static in.ecomexpress.sathi.utils.WatermarkUtils.compressImage;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getResizedBitmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityCameraFaceFilterBinding;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.custom_view.LocationCapture;

@AndroidEntryPoint
public class CameraXActivity extends BaseActivity<ActivityCameraFaceFilterBinding, CameraXViewModel> implements CameraNavigator {

    private ActivityCameraFaceFilterBinding activityMainBinding;
    private PreviewView viewFinder;
    private View greenBoxView;
    private View redBoxView;
    private View squareShape;
    private ImageView afterImage;
    private ExecutorService cameraExecutor;
    private CameraXViewModel viewModel;
    private final int cameraPermissionRequestCode = 11;
    private String empCode = "";
    String fileName, imageCode;
    private Uri selectedPhotoPath;
    private File cacheFile;
    private File fileDir;
    Bitmap startTripImageBitmap;
    double latitude;
    double longitude;
    @Inject
    CameraXViewModel cameraXViewModel;

    private LocationCapture mLocation;
    private String camera_type, file_type;
    public static int imageCaptureCount = 0;
    private ImageHandler imageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activityMainBinding = getViewDataBinding();
        activityMainBinding = ActivityCameraFaceFilterBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        viewFinder = activityMainBinding.viewFinder;
        greenBoxView = activityMainBinding.greenRingView;
        redBoxView = activityMainBinding.redRingView;
        squareShape = activityMainBinding.squareContainer;
        afterImage = activityMainBinding.afterImage;

        mLocation = new LocationCapture(this);
        mLocation.setBlurRadius(5000);
        if (!mLocation.hasLocationEnabled()) {
            if (!cameraXViewModel.getDataManager().getTripId().equalsIgnoreCase("0") && !cameraXViewModel.getDataManager().getIsAdmEmp()) {
                in.ecomexpress.geolocations.LocationTracker.getInstance(CameraXActivity.this, CameraXActivity.this, true, false).runLocationServices();
            }
        }

        cameraXViewModel.setNavigator(this);
        camera_type = getIntent().getExtras().getString(Constants.CAMERA_TYPE);

        if (camera_type.equalsIgnoreCase("selfie_helmet")) {
            file_type = "helmetTrip";
            activityMainBinding.tvHeader.setText(getString(R.string.selfiewithhelmet));
            activityMainBinding.captureImg.setVisibility(View.GONE);
        } else if (camera_type.equalsIgnoreCase("stop_meter_reading")) {
            file_type = "stopTrip";
            activityMainBinding.tvHeader.setText(getString(R.string.capture_vehicle_meter_image));
        } else {
            file_type = "startTrip";
            activityMainBinding.tvHeader.setText(getString(R.string.capture_vehicle_meter_image));
        }

        activityMainBinding.backArrow.setOnClickListener(v -> {
            finish();
            applyTransitionToBackFromActivity(this);
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
        requestCameraPermission();


    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, cameraPermissionRequestCode);
        } else {
            new Handler().postDelayed(() -> startCamera(), 1000);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CameraXActivity.this);
        FaceDetector faceAnalyzer = new FaceDetector(squareShape, greenBoxView, redBoxView, activityMainBinding.captureImg);
        ImageCapture.Builder builder = new ImageCapture.Builder();
        ImageCapture imageCapture = builder.build();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                CameraSelector cameraSelector;

                if (camera_type.equalsIgnoreCase("selfie_helmet")) {
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                } else {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                }


                cameraProvider.unbindAll();

                // Set zoom ratio
                if (camera_type.equalsIgnoreCase("selfie_helmet")) {
                    try {
                        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                        imageAnalysis.setAnalyzer(cameraExecutor, faceAnalyzer);
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture).getCameraControl().setZoomRatio(1.0f).addListener(() -> {
                        }, ContextCompat.getMainExecutor(this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture).getCameraControl().setZoomRatio(1.0f).addListener(() -> {
                        }, ContextCompat.getMainExecutor(this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(this, "Use case binding failed!", Toast.LENGTH_SHORT).show();
            }

        }, ContextCompat.getMainExecutor(this));


        activityMainBinding.captureImg.setOnClickListener(v -> {
            try {
                activityMainBinding.captureImg.setEnabled(false);
                String startTripTimestamp = String.valueOf(System.currentTimeMillis());
                Constants.startTripTimestamp = startTripTimestamp;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress1);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    cacheFile = new File(fileDir, cameraXViewModel.getDataManager().getEmp_code() + "_" + Constants.startTripTimestamp + "_" + file_type + ".jpg");
                    if (!cacheFile.createNewFile()) {
                        throw new IOException("Failed to create new file");
                    }
                    //selectedPhotoPath = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cacheFile);
                    selectedPhotoPath = Uri.fromFile(cacheFile);

                } else {
                    fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress1);
                    if (!fileDir.exists()) fileDir.mkdirs();
                    cacheFile = new File(fileDir, cameraXViewModel.getDataManager().getEmp_code() + "_" + Constants.startTripTimestamp + "_" + file_type + ".jpg");
                    if (!cacheFile.createNewFile()) {
                        throw new IOException("Failed to create new file");
                    }
                    selectedPhotoPath = Uri.fromFile(cacheFile);
                }

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(cacheFile).build();
                imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try {

                                afterImage.setVisibility(View.VISIBLE);
                                viewFinder.setVisibility(View.GONE);
                                redBoxView.setVisibility(View.GONE);
                                greenBoxView.setVisibility(View.GONE);
                                squareShape.setVisibility(View.GONE);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoPath);

                                Matrix matrix;
                                if (camera_type.equalsIgnoreCase("selfie_helmet")) {
                                    matrix = new Matrix();
                                    matrix.postRotate(270F);
                                } else {
                                    matrix = new Matrix();
                                    matrix.postRotate(90F);
                                }

                                Bitmap bmp = rotateImageIfRequired(bitmap, selectedPhotoPath.getPath());

                                afterImage.setImageBitmap(bmp);
                                showStatusDialog();

                            } catch (Exception e) {
                                Toast.makeText(CameraXActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        enableViewAfterDelay(activityMainBinding.captureImg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraXActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        exception.printStackTrace();
                        enableViewAfterDelay(activityMainBinding.captureImg);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
                enableViewAfterDelay(activityMainBinding.captureImg);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraPermissionRequestCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Please give the permission first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public CameraXViewModel getViewModel() {
        // TODO: Return the ViewModel instance
        return cameraXViewModel;
    }

    @Override
    public int getBindingVariable() {
        // TODO: Return the Binding Variable ID
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_camera_face_filter;
    }

    private void showStatusDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_selfie_clear);
        dialog.setCancelable(false);
        Button btnUpload = dialog.findViewById(R.id.btn_upload);
        TextView btnRetake = dialog.findViewById(R.id.btn_retake);

        dialog.show();

        if (btnUpload != null) {
            btnUpload.setOnClickListener(v -> {
                btnUpload.setEnabled(false);
                setImage();
                enableViewAfterDelay(btnUpload);
                dialog.dismiss();
            });

        }


        if (btnRetake != null) {
            btnRetake.setOnClickListener(v -> {
                afterImage.setVisibility(View.GONE);
                viewFinder.setVisibility(View.VISIBLE);
                if (camera_type.equalsIgnoreCase("selfie_helmet")) {
                    redBoxView.setVisibility(View.VISIBLE);
                    greenBoxView.setVisibility(View.VISIBLE);
                }
                squareShape.setVisibility(View.VISIBLE);
                dialog.dismiss();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }


    public void setImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoPath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream); // 0=lowest, 100=highest quality
            byte[] byteArray = stream.toByteArray();
            Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap resizeBitmap = getResizedBitmap(yourCompressBitmap, 1200);
            //activityMainBinding.afterImage.setImageBitmap(resizeBitmap);
            File fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress1);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File cacheFile = new File(fileDir, cameraXViewModel.getDataManager().getEmp_code() + "_" + Constants.startTripTimestamp + "_" + file_type + ".jpg");
            String filePath = compressImage(this, cacheFile);
            WaterMark waterMark = new WaterMark();
            waterMark.emp_code = cameraXViewModel.getDataManager().getEmp_code();
            waterMark.setLat(String.valueOf(cameraXViewModel.getDataManager().getCurrentLatitude()));
            waterMark.setLng(String.valueOf(cameraXViewModel.getDataManager().getCurrentLongitude()));
            waterMark.setEcom_text("Ecom Express Ltd");
            waterMark.date = String.valueOf(Calendar.getInstance().getTime());
            Bitmap bitmap_with_watermark = applyWaterMark(resizeBitmap, cameraXViewModel.getDataManager().getEmp_code(), waterMark);
            CryptoUtils.encryptFile(filePath, filePath, Constants.ENC_DEC_KEY);
            imageCaptureResult(bitmap_with_watermark, filePath, mLocation.getLatitude(), mLocation.getLongitude(), file_type);


        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void imageCaptureResult(Bitmap bitmap, String imagePath, double latitude, double longitude, String imageCode) {
        fileName = imagePath;
        startTripImageBitmap = bitmap;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageCode = imageCode;
        uploadS3Object(fileName, imageCode);
    }

    private void uploadS3Object(String fileName, String imageCode) {
        cameraXViewModel.uploadImageServer(CameraXActivity.this, fileName, imageCode, Constants.startTripTimestamp);
    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void onErrorMessage(String message) {
        showSnackbar(message);
    }

    @Override
    public void imageUploadNotify(String imgId, String fileName, String imgUri) {

        Intent intent = new Intent();
        intent.putExtra("URI", selectedPhotoPath.toString());
        intent.putExtra("imgId", imgId);
        intent.putExtra("fileName", fileName);
        intent.putExtra("imgUri", imgUri);
        if (camera_type.equalsIgnoreCase("selfie_helmet")) {
            setResult(3, intent);
        } else {
            setResult(2, intent);
        }

        finish();


    }

    private void enableViewAfterDelay(View view) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> view.setEnabled(true), 3000);
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap, String imagePath) throws IOException {
        ExifInterface exifInterface = new ExifInterface(imagePath);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}


package in.ecomexpress.sathi.utils.cameraView;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.WatermarkUtils.applyWaterMark;
import static in.ecomexpress.sathi.utils.WatermarkUtils.compressImage;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getResizedBitmap;

import android.Manifest;
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
import android.util.Log;
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
import in.ecomexpress.sathi.databinding.ActivityCameraMapBinding;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.custom_view.LocationCapture;

@AndroidEntryPoint
public class CameraSelfieActivity extends BaseActivity<ActivityCameraMapBinding, CameraXViewModel> {

    private ActivityCameraMapBinding activityCameraMapBinding;
    private PreviewView viewFinder;
    private View greenBoxView;
    private View redBoxView;
    private View squareShape;
    private ImageView afterImage;
    private ExecutorService cameraExecutor;
    private CameraXViewModel viewModel;
    private final int cameraPermissionRequestCode = 11;
    private String empCode = "";
    private Uri selectedPhotoPath;
    private File cacheFile;
    private File fileDir;
    private String camera_type;
    private String file_type = "SelfieImage";
    private LocationCapture mLocation;
    String fileName, imageCode;
    Bitmap startTripImageBitmap;
    double latitude;
    double longitude;


    @Inject
    CameraXViewModel cameraXViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityCameraMapBinding = getViewDataBinding();

        activityCameraMapBinding = ActivityCameraMapBinding.inflate(getLayoutInflater());
        setContentView(activityCameraMapBinding.getRoot());
        cameraXViewModel.getDataManager().setScreenStatus(true);
        cameraXViewModel.getDataManager().setCheckAttendanceLoginStatus(true);
        cameraXViewModel.getAllApiUrl();

        viewFinder = activityCameraMapBinding.viewFinder;
        greenBoxView = activityCameraMapBinding.greenRingView;
        redBoxView = activityCameraMapBinding.redRingView;
        squareShape = activityCameraMapBinding.squareContainer;
        afterImage = activityCameraMapBinding.afterImage;
        cameraExecutor = Executors.newSingleThreadExecutor();

        requestCameraPermission();
        activityCameraMapBinding.backArrow.setOnClickListener(v -> {
            finish();
            applyTransitionToBackFromActivity(this);
        });
    }

   /* private void setBackPressed() {
        activityCameraMapBinding.backArrow.setOnClickListener(view -> CameraSelfieActivity.super.onBackPressed());
    }*/

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    cameraPermissionRequestCode
            );
        } else {
            new Handler().postDelayed(() -> startCamera(), 1000);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CameraSelfieActivity.this);
        FaceDetector faceAnalyzer = new FaceDetector(squareShape, greenBoxView, redBoxView, activityCameraMapBinding.captureImg);
        ImageCapture.Builder builder = new ImageCapture.Builder();
        ImageCapture imageCapture = builder.build();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, faceAnalyzer);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                        imageCapture
                );

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);

                // Set zoom ratio
                try {
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture)
                            .getCameraControl()
                            .setZoomRatio(1.0f)
                            .addListener(() -> {
                            }, ContextCompat.getMainExecutor(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Use case binding failed!", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));

        activityCameraMapBinding.captureImg.setOnClickListener(v -> {
            try {
                activityCameraMapBinding.captureImg.setEnabled(false);
                String startTripTimestamp = String.valueOf(System.currentTimeMillis());
                Constants.startTripTimestamp = startTripTimestamp;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress1);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    cacheFile = new File(fileDir, startTripTimestamp + "_" + cameraXViewModel.getDataManager().getEmp_code() + "_" + file_type + ".jpg");
                    if (!cacheFile.createNewFile()) {
                        throw new IOException("Failed to create new file");
                    }
                    //selectedPhotoPath = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", cacheFile);
                    selectedPhotoPath = Uri.fromFile(cacheFile);

                } else {
                    fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress1);
                    if (!fileDir.exists()) fileDir.mkdirs();
                    cacheFile = new File(fileDir, startTripTimestamp + "_" + cameraXViewModel.getDataManager().getEmp_code() + "_" + file_type + ".jpg");
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
                                Matrix matrix = new Matrix();
                                matrix.postRotate(270F);
                                Bitmap bmp = rotateImageIfRequired(bitmap, selectedPhotoPath.getPath());
                                afterImage.setImageBitmap(bmp);
                                showStatusDialog();
                            } catch (Exception e) {
                                Toast.makeText(CameraSelfieActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        enableViewAfterDelay(activityCameraMapBinding.captureImg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraSelfieActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        exception.printStackTrace();
                        enableViewAfterDelay(activityCameraMapBinding.captureImg);
                    }
                });
            } catch (Exception e) {
                enableViewAfterDelay(activityCameraMapBinding.captureImg);
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
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
                Uri path = selectedPhotoPath;
                Log.e("uri", "" + path.getPath());
                ImageUpload();
                enableViewAfterDelay(btnUpload);
                dialog.dismiss();


            });
        }

        if (btnRetake != null) {
            btnRetake.setOnClickListener(v -> {
                afterImage.setVisibility(View.GONE);
                viewFinder.setVisibility(View.VISIBLE);
                redBoxView.setVisibility(View.VISIBLE);
                greenBoxView.setVisibility(View.VISIBLE);
                squareShape.setVisibility(View.VISIBLE);
                dialog.dismiss();
            });
        }
    }

    private void ImageUpload() {
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
            File cacheFile = new File(fileDir, Constants.startTripTimestamp + "_" + cameraXViewModel.getDataManager().getEmp_code() + "_" + file_type + ".jpg");
            String filePath = compressImage(this, cacheFile);
            WaterMark waterMark = new WaterMark();
            waterMark.emp_code = cameraXViewModel.getDataManager().getEmp_code();
            waterMark.setLat(String.valueOf(cameraXViewModel.getDataManager().getCurrentLatitude()));
            waterMark.setLng(String.valueOf(cameraXViewModel.getDataManager().getCurrentLongitude()));
            waterMark.setEcom_text("Ecom Express Ltd");
            waterMark.date = String.valueOf(Calendar.getInstance().getTime());
            Bitmap bitmap_with_watermark = applyWaterMark(resizeBitmap, cameraXViewModel.getDataManager().getEmp_code(), waterMark);
            CryptoUtils.encryptFile(filePath, filePath, Constants.ENC_DEC_KEY);
            imageCaptureResult(bitmap_with_watermark, filePath);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void imageCaptureResult(Bitmap bitmap, String imagePath) {
        fileName = imagePath;
        startTripImageBitmap = bitmap;
        uploadS3Object(fileName);
    }

    private void uploadS3Object(String fileName) {
        cameraXViewModel.uploadSelfieImageServer(CameraSelfieActivity.this, fileName);
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
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public CameraXViewModel getViewModel() {
        return cameraXViewModel;
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_camera_map;
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
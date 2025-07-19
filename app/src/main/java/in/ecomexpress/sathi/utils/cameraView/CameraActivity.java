package in.ecomexpress.sathi.utils.cameraView;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.WatermarkUtils.applyWaterMark;
import static in.ecomexpress.sathi.utils.WatermarkUtils.compressImage;
import static in.ecomexpress.sathi.utils.WatermarkUtils.getResizedBitmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityCameraFaceFilterBinding;
import in.ecomexpress.sathi.repo.remote.model.masterdata.WaterMark;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.CryptoUtils;

@AndroidEntryPoint
public class CameraActivity extends AppCompatActivity {

    private ActivityCameraFaceFilterBinding activityMainBinding;
    private PreviewView viewFinder;
    private View squareShape;
    private ImageView afterImage;
    private ExecutorService cameraExecutor;
    private final int cameraPermissionRequestCode = 11;
    String fileName, imageCode;
    private Uri selectedPhotoPath;
    private File cacheFile;
    private File fileDir;
    Bitmap bitmap;
    String empCode;
    String latitude;
    String longitude;
    String ImageCode;
    String ImageName;
    String awbNumber;
    String drs_id;
    private String activitySource = "";

    Boolean isCrop;
    private final int CAMERA_SCANNER_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityCameraFaceFilterBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        viewFinder = activityMainBinding.viewFinder;
        squareShape = activityMainBinding.squareContainer;
        afterImage = activityMainBinding.afterImage;
        activityMainBinding.backArrow.setOnClickListener(v -> {
            finish();
            applyTransitionToOpenActivity(this);
        });
        cameraExecutor = Executors.newSingleThreadExecutor();
        requestCameraPermission();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        empCode = intent.getStringExtra("EmpCode");
        latitude = String.valueOf(intent.getDoubleExtra("Latitude", 0L));
        longitude = String.valueOf(intent.getDoubleExtra("Longitude", 0L));
        ImageCode = intent.getStringExtra("ImageCode");
        ImageName = intent.getStringExtra("imageName");
        awbNumber = intent.getStringExtra("awbNumber");
        drs_id = intent.getStringExtra("drs_id");
        isCrop = intent.getBooleanExtra("isCrop", false);
        activitySource = intent.getExtras().getString("activitySource","");
        setupUIwork();
    }

    private void setupUIwork(){
        if(activitySource.equalsIgnoreCase("Product")){
            activityMainBinding.tvHeader.setText(getString(R.string.capture_product_image));
        } else if (activitySource.equalsIgnoreCase("Flyer")) {
            activityMainBinding.tvHeader.setText(getString(R.string.captured_flyer_image));
        } else{
            activityMainBinding.tvHeader.setText(getString(R.string.capture_image));
        }
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, cameraPermissionRequestCode);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CameraActivity.this);
        ImageCapture.Builder builder = new ImageCapture.Builder();
        ImageCapture imageCapture = builder.build();
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                try {
                    cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                CommonUtils.showCustomSnackbar(activityMainBinding.getRoot(), "Use Case Binding Failed" + e.getMessage(), this);
            }
        }, ContextCompat.getMainExecutor(this));

        activityMainBinding.captureImg.setOnClickListener(v -> {
            activityMainBinding.captureImg.setEnabled(false);
            ImageViewCompat.setImageTintList(activityMainBinding.captureImg, ColorStateList.valueOf(ContextCompat.getColor(CameraActivity.this, R.color.gray)));
            try {
                Constants.startTripTimestamp = String.valueOf(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress1);
                } else {
                    fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress1);
                }
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                cacheFile = new File(fileDir, awbNumber + "_" + drs_id + "_" + ImageCode + ".jpg");
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }
                if (!cacheFile.createNewFile()) {
                    throw new IOException("Failed to create new file");
                }
                selectedPhotoPath = Uri.fromFile(cacheFile);
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(cacheFile).build();

                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        try {
                            afterImage.setVisibility(View.VISIBLE);
                            viewFinder.setVisibility(View.GONE);
                            squareShape.setVisibility(View.GONE);
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedPhotoPath);
                            bitmap = rotateImageIfRequired(bitmap, selectedPhotoPath.getPath());
                            afterImage.setImageBitmap(bitmap);
                            showStatusDialog();
                        } catch (Exception e) {
                            Toast.makeText(CameraActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        removeTintColor();
                        enableViewAfterDelay(activityMainBinding.captureImg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        CommonUtils.showCustomSnackbar(activityMainBinding.getRoot(), "Image Not Captured Properly Recaptured It Again", CameraActivity.this);
                        removeTintColor();
                        enableViewAfterDelay(activityMainBinding.captureImg);
                    }
                });
            } catch (Exception e) {
                removeTintColor();
                enableViewAfterDelay(activityMainBinding.captureImg);
                e.printStackTrace();
            }
        });
    }

    private void removeTintColor() {
        ImageViewCompat.setImageTintList(activityMainBinding.captureImg, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == cameraPermissionRequestCode && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            CommonUtils.showCustomSnackbar(activityMainBinding.getRoot(), "Grant Camera Permission For Capture Image", this);
        }
    }

    private void showStatusDialog() {
        try{
            BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.otpsheetDialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottomsheet_camera);
            dialog.setCancelable(false);
            TextView bottomSheetHeading = dialog.findViewById(R.id.bottomSheetHeading);
            TextView instructionText = dialog.findViewById(R.id.instructionText);
            if (activitySource.equalsIgnoreCase("Flyer")) {
                if (bottomSheetHeading != null) {
                    bottomSheetHeading.setText(getString(R.string.is_your_flyer_image_clear));
                }
                if (instructionText != null) {
                    instructionText.setText(getString(R.string.make_sure_the_flyer_image_is_clear_so_we_can_verify_it_easily));
                }
            } else if (activitySource.equalsIgnoreCase("Product")) {
                if (bottomSheetHeading != null) {
                    bottomSheetHeading.setText(getString(R.string.is_your_product_image_clear));
                }
                if (instructionText != null) {
                    instructionText.setText(getString(R.string.make_sure_the_product_image_is_clear_so_we_can_verify_it_easily));
                }
            } else{
                if (bottomSheetHeading != null) {
                    bottomSheetHeading.setText(getString(R.string.is_captured_image_clear));
                }
                if (instructionText != null) {
                    instructionText.setText(getString(R.string.make_sure_the_captured_image_is_clear_so_we_can_verify_it_easily));
                }
            }
            Button btnUpload = dialog.findViewById(R.id.btn_upload);
            TextView btnRetake = dialog.findViewById(R.id.btn_retake);
            dialog.show();
            if (btnUpload != null) {
                btnUpload.setOnClickListener(v -> {
                    btnUpload.setEnabled(false);
                    if (isCrop) {
                        startActivityForResult(CropImage.activity(selectedPhotoPath).getIntent(this), CAMERA_SCANNER_CODE);
                        dialog.dismiss();
                    } else {
                        setImage();
                        dialog.dismiss();
                        //enableViewAfterDelay(btnUpload);
                    }
                });
            }

            if (btnRetake != null) {
                btnRetake.setOnClickListener(v -> {
                    btnRetake.setEnabled(false);
                    afterImage.setVisibility(View.GONE);
                    viewFinder.setVisibility(View.VISIBLE);
                    squareShape.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void enableViewAfterDelay(View view) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> view.setEnabled(true), 3000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    public void setImage() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap resizeBitmap = getResizedBitmap(yourCompressBitmap, 1200);
            File fileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/" + Constants.EcomExpress1);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File cacheFile = new File(fileDir, awbNumber + "_" + drs_id + "_" + ImageCode + ".jpg");
            String filePath = compressImage(this, cacheFile);
            WaterMark waterMark = new WaterMark();
            waterMark.emp_code = empCode;
            waterMark.setLat(String.valueOf(latitude));
            waterMark.setLng(String.valueOf(longitude));
            waterMark.setEcom_text("Ecom Express Ltd");
            waterMark.date = String.valueOf(Calendar.getInstance().getTime());
            Bitmap bitmap_with_watermark = applyWaterMark(resizeBitmap, empCode, waterMark);
            saveBitmapWithWatermark(filePath, fileDir, bitmap_with_watermark,ImageCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveBitmapWithWatermark(String filePath, File fileDir, Bitmap bitmap, String imageCode) {
        File watermarkedFile = new File(fileDir, awbNumber + "_" + drs_id + "_" + imageCode + "_watermarked.jpg");
        try (FileOutputStream out = new FileOutputStream(watermarkedFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
            String imagePathWithWaterMark = watermarkedFile.getAbsolutePath();
            CryptoUtils.encryptFile(filePath, filePath, Constants.ENC_DEC_KEY);
            imageCaptureResult(imagePathWithWaterMark, filePath, ImageCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imageCaptureResult(String imagePathWithWaterMark, String imagePath, String imageCode) {
        fileName = imagePath;
        this.imageCode = imageCode;
        backWithImage(imagePathWithWaterMark, fileName, imageCode);
    }

    private void backWithImage(String imagePathWithWaterMark, String imagePath, String imageCode) {
        Intent intent = new Intent();
        intent.putExtra("imageUri", imagePath);
        intent.putExtra("imageCode", imageCode);
        intent.putExtra("imageName", ImageName);
        intent.putExtra("imagePathWithWaterMark", imagePathWithWaterMark);
        setResult(RESULT_OK, intent);
        finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && isCrop) {
            Uri resultUri = Objects.requireNonNull(CropImage.getActivityResult(data)).getUri();
            Intent intent = new Intent();
            intent.putExtra("croped_path", resultUri.getPath());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
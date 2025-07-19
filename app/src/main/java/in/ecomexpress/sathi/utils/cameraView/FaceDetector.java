package in.ecomexpress.sathi.utils.cameraView;


import android.view.View;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class FaceDetector implements ImageAnalysis.Analyzer {

    private final View squareShape;
    private final View greenBoxView;
    private final View redBoxView;
    private final View captureImg;

    // Face detection options
    private final com.google.mlkit.vision.face.FaceDetector faceDetector;

    public FaceDetector(View squareShape, View greenBoxView, View redBoxView, View captureImg) {
        this.squareShape = squareShape;
        this.greenBoxView = greenBoxView;
        this.redBoxView = redBoxView;
        this.captureImg = captureImg;
        faceDetector = FaceDetection.getClient(
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .enableTracking()
                        .build()
        );


    }

    public void stopDetector() {
        faceDetector.close();
        greenBoxView.setVisibility(View.INVISIBLE);
        redBoxView.setVisibility(View.INVISIBLE);
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void analyze(ImageProxy imageProxy) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        // Convert ImageProxy to InputImage
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        // Process the image using the face detector
        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    processFaces(faces);
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    imageProxy.close();
                    e.printStackTrace();
                });
    }

    // Show the green box
    private void showGreenBox() {
        greenBoxView.setVisibility(View.VISIBLE);
        redBoxView.setVisibility(View.INVISIBLE);
    }

    private void captureImgShow() {
        captureImg.setVisibility(View.VISIBLE);
    }

    private void captureImgHide() {
        captureImg.setVisibility(View.GONE);
    }

    // Show the red box
    private void showRedBox() {
        greenBoxView.setVisibility(View.INVISIBLE);
        redBoxView.setVisibility(View.VISIBLE);
    }

    // Determine if liveness is detected based on facial features
    private boolean isLivenessDetected(List<Face> faces) {
        Face face = faces.get(0);

        // Retrieve face contours
        FaceContour faceContour = face.getContour(FaceContour.FACE);
        FaceContour leftEyeContour = face.getContour(FaceContour.LEFT_EYE);
        FaceContour rightEyeContour = face.getContour(FaceContour.RIGHT_EYE);
        FaceContour noseBridgeContour = face.getContour(FaceContour.NOSE_BRIDGE);
        FaceContour noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM);
        FaceContour leftEyebrowTopContour = face.getContour(FaceContour.LEFT_EYEBROW_TOP);
        FaceContour leftEyebrowBottomContour = face.getContour(FaceContour.LEFT_EYEBROW_BOTTOM);
        FaceContour rightEyebrowTopContour = face.getContour(FaceContour.RIGHT_EYEBROW_TOP);
        FaceContour rightEyebrowBottomContour = face.getContour(FaceContour.RIGHT_EYEBROW_BOTTOM);
        FaceContour upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP);
        FaceContour upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM);
        FaceContour lowerLipTopContour = face.getContour(FaceContour.LOWER_LIP_TOP);
        FaceContour lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM);
        FaceContour leftCheekCenterContour = face.getContour(FaceContour.LEFT_CHEEK);
        FaceContour rightCheekCenterContour = face.getContour(FaceContour.RIGHT_CHEEK);

        // Head rotations
        float rotX = face.getHeadEulerAngleX();
        float rotY = face.getHeadEulerAngleY();
        float rotZ = face.getHeadEulerAngleZ();

        // Customize the threshold values accordingly
        float rotationThresholdX = 10f;
        float rotationThresholdY = 12f;
        float rotationThresholdZ = 15f;

        boolean isHeadRotatedX = rotX <= rotationThresholdX && rotX >= -rotationThresholdX;
        boolean isHeadRotatedY = rotY <= rotationThresholdY && rotY >= -rotationThresholdY;
        boolean isHeadRotatedZ = rotZ <= rotationThresholdZ && rotZ >= -rotationThresholdZ;

        boolean isLeftEyeOpen = (face.getLeftEyeOpenProbability() != null) && (face.getLeftEyeOpenProbability() > 0.5f);
        boolean isRightEyeOpen = (face.getRightEyeOpenProbability() != null) && (face.getRightEyeOpenProbability() > 0.5f);
        boolean isSmile = (face.getSmilingProbability() != null) && (face.getSmilingProbability() > 0.003f);

        boolean areAllLandmarksVisible = faceContour != null
                && leftEyeContour != null
                && rightEyeContour != null
                && noseBridgeContour != null
                && noseBottomContour != null
                && leftEyebrowTopContour != null
                && leftEyebrowBottomContour != null
                && rightEyebrowTopContour != null
                && rightEyebrowBottomContour != null
                && upperLipTopContour != null
                && upperLipBottomContour != null
                && lowerLipTopContour != null
                && lowerLipBottomContour != null
                && leftCheekCenterContour != null
                && rightCheekCenterContour != null;

        return areAllLandmarksVisible && isHeadRotatedX && isHeadRotatedY && isHeadRotatedZ && isLeftEyeOpen && isRightEyeOpen && isSmile;
    }

    // Process the detected faces
    private void processFaces(List<Face> faces) {
        int numberOfFaces = faces.size();

        squareShape.post(() -> {
            if (numberOfFaces == 1) {
                if (isLivenessDetected(faces)) {
                    // If real, show the green box
                    showGreenBox();
                    captureImgShow();
                } else {
                    // If spoof, show the red box
                    showRedBox();
                    captureImgHide();
                }
            } else if (numberOfFaces > 1) {
                // If more than one face, show the red box
                showRedBox();
                captureImgHide();
            } else {
                // If no face, show the red box
                showRedBox();
                captureImgHide();
            }
        });
    }
}

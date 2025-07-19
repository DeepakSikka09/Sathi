package in.ecomexpress.sathi.utils.cameraView;

public interface CameraNavigator {

    void showError(String error);

    void onErrorMessage(String message);

    void imageUploadNotify(String imgId, String fileName, String imgUri);


}

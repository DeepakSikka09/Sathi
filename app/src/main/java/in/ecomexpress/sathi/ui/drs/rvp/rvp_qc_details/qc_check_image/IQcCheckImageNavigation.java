package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image;

public interface IQcCheckImageNavigation {
    void captureImage(String status);

    void showImagePreview();

    void showError(String e);
}

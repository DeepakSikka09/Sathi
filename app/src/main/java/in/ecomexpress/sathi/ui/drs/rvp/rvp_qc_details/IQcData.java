package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details;


import in.ecomexpress.sathi.ui.base.BaseFragment;

public interface IQcData {
    void getData(BaseFragment fragment);
    boolean validateData();
    boolean getPreviewImageClicked();
    void validate(boolean flag);

}
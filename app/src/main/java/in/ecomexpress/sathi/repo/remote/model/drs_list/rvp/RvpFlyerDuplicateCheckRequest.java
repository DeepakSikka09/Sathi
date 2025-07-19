package in.ecomexpress.sathi.repo.remote.model.drs_list.rvp;

public class RvpFlyerDuplicateCheckRequest {

    private String drs_id;

    private String ref_packaging_barcode;

    private String awb;

    public String getDrs_id () {
        return drs_id;
    }

    public void setDrs_id (String drs_id) {
        this.drs_id = drs_id;
    }

    public String getRef_packaging_barcode () {
        return ref_packaging_barcode;
    }

    public void setRef_packaging_barcode (String ref_packaging_barcode) {
        this.ref_packaging_barcode = ref_packaging_barcode;
    }

    public String getAwb () {
        return awb;
    }

    public void setAwb (String awb) {
        this.awb = awb;
    }
}
package in.ecomexpress.sathi.repo.local.db.model;

import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;

/**
 * Created by shivangis on 1/7/2019.
 */

public class RVPUndeliveredReasonCodeList {
    public RVPReasonCodeMaster getRvpReasonCodeMaster() {
        return rvpReasonCodeMaster;
    }

    public void setRvpReasonCodeMaster(RVPReasonCodeMaster rvpReasonCodeMaster) {
        this.rvpReasonCodeMaster = rvpReasonCodeMaster;
    }

    private RVPReasonCodeMaster rvpReasonCodeMaster;


    public RVPUndeliveredReasonCodeList(RVPReasonCodeMaster rvpReasonCodeMaster) {

        this.rvpReasonCodeMaster = rvpReasonCodeMaster;
    }


}

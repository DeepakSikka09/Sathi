package in.ecomexpress.sathi.repo.local.db.model;

import in.ecomexpress.sathi.repo.remote.model.masterdata.ForwardReasonCodeMaster;

public class ForwardUndeliveredReasonCodeList {

    private ForwardReasonCodeMaster forwardReasonCodeMaster;


    public ForwardUndeliveredReasonCodeList(ForwardReasonCodeMaster forwardReasonCodeMaster) {

        this.forwardReasonCodeMaster = forwardReasonCodeMaster;
    }

    public ForwardReasonCodeMaster getForwardReasonCodeMaster() {
        return forwardReasonCodeMaster;
    }

    public void setForwardReasonCodeMaster(ForwardReasonCodeMaster forwardReasonCodeMaster) {
        this.forwardReasonCodeMaster = forwardReasonCodeMaster;
    }
}

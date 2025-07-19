package in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list;

/**
 * Created by shivangis on 9/6/2018.
 */

public class QcFailItemViewModel {

    public String rvpCommit;
    public String status;
    public String mandatory;

    public QcFailItemViewModel(String wizard, String status, String mandatory) {
        this.rvpCommit = wizard;
        this.mandatory = mandatory;
        this.status = status;
    }

    public String qcitem() {
        return rvpCommit;
    }

    public String mandatoryCheck() {
        return mandatory;
    }

    public String qcStatus() {
        return status;
    }
}

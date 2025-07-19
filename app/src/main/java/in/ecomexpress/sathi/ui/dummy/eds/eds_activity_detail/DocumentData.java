package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;

/**
 * Created by dhananjayk on 10-11-2018.
 */

public interface DocumentData {
    void setDetail(int position, EDSActivityWizard edsActivityWizard);
    void setAadharToCamera();
    String getFrontImageCode();
    String getRearImageCode();

}

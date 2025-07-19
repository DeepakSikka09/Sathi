package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import in.ecomexpress.sathi.databinding.ActivityEdsDetailBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.ui.base.BaseFragment;

/**
 * Created by dhananjayk on 10-11-2018.
 */

public interface ActivityData {
    void getData(BaseFragment fragment);
    boolean validateData();
    void validate(boolean flag);
    boolean validateCancelData();
    void setImageValidation();
    EDSActivityWizard getActivityWizard();

}

package in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list;

import java.util.List;

import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;

/**
 * Created by parikshittomar on 20-08-2018.
 */

public interface IRVPQcFailureNavigator {

    void onBack();

    void onSetAdapter(List<RvpCommit.QcWizard> qcWizardList);

    void onNext();
}

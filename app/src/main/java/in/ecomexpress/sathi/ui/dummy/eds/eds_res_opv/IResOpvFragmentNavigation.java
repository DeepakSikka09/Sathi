package in.ecomexpress.sathi.ui.dummy.eds.eds_res_opv;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;

/**
 * Created by dhananjayk on 29-03-2019.
 */

public interface IResOpvFragmentNavigation {
    void shareOPVMasterData(List<GeneralQuestion> generalQuestions);

    void showError(String e);
}

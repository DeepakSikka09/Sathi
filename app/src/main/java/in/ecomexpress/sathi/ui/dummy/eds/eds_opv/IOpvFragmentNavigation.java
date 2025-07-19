package in.ecomexpress.sathi.ui.dummy.eds.eds_opv;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;

/**
 * Created by dhananjayk on 29-11-2018.
 */

public interface IOpvFragmentNavigation {
    void shareOPVMasterData(List<GeneralQuestion> generalQuestions);

    void showError(String e);
}

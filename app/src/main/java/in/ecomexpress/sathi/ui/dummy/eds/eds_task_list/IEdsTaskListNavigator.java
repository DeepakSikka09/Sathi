package in.ecomexpress.sathi.ui.dummy.eds.eds_task_list;

import java.util.LinkedHashMap;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_response;

/**
 * Created by dhananjayk on 27-10-2018.
 */

public interface IEdsTaskListNavigator {
    void onExpandableData(boolean flag,List<String> header,LinkedHashMap<String, List<String>> childList,List<Boolean> option ,LinkedHashMap<String, Boolean> childList_optional_flag );
    void onProceed();
    void onCancel();
    void onBack();

    void showMsg();

    void sendBiometricResponse(Biometric_response biometric_response);

    void showError(String e);
}

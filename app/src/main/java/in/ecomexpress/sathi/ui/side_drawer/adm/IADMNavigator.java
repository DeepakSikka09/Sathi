package in.ecomexpress.sathi.ui.side_drawer.adm;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMDATA;
import in.ecomexpress.sathi.repo.remote.model.adm.ADMUpdateResponse;

public interface IADMNavigator {

    void sendADMResponse(List<ADMDATA> response);

    Context getActivityContext();

    void setTimer1();

    void setTimer2();

    void setTimer3();

    void setTimer4();

    void setTimer5();

    void setTimer6();

    void setTimer7();

    void setUpdatedData();

    void chooseSpinner1(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner2(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner3(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner4(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner5(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner6(AdapterView<?> parent, View view, int pos, long id);

    void chooseSpinner7(AdapterView<?> parent, View view, int pos, long id);

    void showInfo(ADMUpdateResponse response);

    void setFinish();
}
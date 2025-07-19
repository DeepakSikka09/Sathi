package in.ecomexpress.sathi.ui.drs.forward.obd.navigator;

import android.app.Activity;
import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;

public interface IFwdOBDCompleteNavigator {

    void onSubmitSuccess(ForwardCommit forwardCommit);

    void OpenFailScreen(ForwardCommit forwardCommit);

    void OnSubmitClick();

    void showError(String error);

    void navigateToNextActivity();

    void commitShipmentAgain();

    void isCall(int isCall);

    void setConsigneeDistance(int meter);

    void setConsigneeProfiling(boolean enable);

    void undeliveredShipment(boolean failFlag, boolean b);

    Activity getActivityContext();

    void clearStack();
}
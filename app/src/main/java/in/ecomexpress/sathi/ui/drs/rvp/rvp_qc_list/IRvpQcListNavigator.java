package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list;

public interface IRvpQcListNavigator {

    void onUnsuccessful();

    void onProceed();

    void OnBack();

    void setLayoutChild2Visibility(boolean isVisible);

    void setHeaderVisibility(boolean flag);

    void showError(String e);
}
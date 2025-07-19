package in.ecomexpress.sathi.ui.dashboard.fuel;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.fuel.response.Reports;

public interface IFuelReimburseNavigator {
    void onHandleError(ErrorResponse errorDetails);

    void onShowDescription(String error);

    void OnSetFuelAdapter(List<Reports> reports);

    void showError(String error);

    void clearStack();

    void onShowLogout(String description);

    void showErrorMessage(boolean status);
}
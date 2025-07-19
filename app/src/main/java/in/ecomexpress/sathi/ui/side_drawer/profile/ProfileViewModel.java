package in.ecomexpress.sathi.ui.side_drawer.profile;

import androidx.databinding.ObservableField;
import android.app.Application;
import android.text.TextUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class ProfileViewModel extends BaseViewModel<IProfileNavigator> {

    private static final String TAG = ProfileViewModel.class.getSimpleName();
    private final ObservableField<String> empCode = new ObservableField<>();
    private final ObservableField<String> empName = new ObservableField<>();
    private final ObservableField<String> empServiceCenter = new ObservableField<>();
    private final ObservableField<String> empLocationCode = new ObservableField<>();
    private final ObservableField<String> empDesignation = new ObservableField<>();
    private final ObservableField<String> empMobile = new ObservableField<>();

    @Inject
    public ProfileViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public ObservableField<String> getCode() {
        return empCode;
    }

    public ObservableField<String> getEmpCodeName() {
        return empName;
    }

    public ObservableField<String> getEmpServiceCenter() {
        return empServiceCenter;
    }

    public ObservableField<String> getEmpLocationCode() {
        return empLocationCode;
    }

    public ObservableField<String> getEmpDesignation() {
        return empDesignation;
    }

    public ObservableField<String> getEmpMobile() {
        return empMobile;
    }

    public void setProfileData() {
        final String Name = getDataManager().getName();
        try {
            if (!TextUtils.isEmpty(Name)) {
                empName.set(Name);
            } else {
                empName.set("Name: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }

        final String code = getDataManager().getCode();
        try {
            if (!TextUtils.isEmpty(code)) {
                empCode.set(code);
            } else {
                empCode.set("Emp Code: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }

        final String serviceCenter = getDataManager().getServiceCenter();
        try {
            if (!TextUtils.isEmpty(serviceCenter)) {
                empServiceCenter.set(serviceCenter);
            } else {
                empServiceCenter.set("Service Center: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }

        final String empLocation = getDataManager().getLocationCode();
        try {
            if (!TextUtils.isEmpty(empLocation)) {
                empLocationCode.set(code);
            } else {
                empLocationCode.set("Location Code: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }

        final String designation = getDataManager().getDesignation();
        try {
            if (!TextUtils.isEmpty(designation)) {
                empDesignation.set(designation);
            } else {
                empDesignation.set("Designation: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }

        final String mobile = getDataManager().getMobile();
        try {
            if (!TextUtils.isEmpty(mobile)) {
                empMobile.set(mobile);
            } else {
                empMobile.set("Mobile: NA");
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void onBackClick() {
        getNavigator().onBackClick();
    }
}
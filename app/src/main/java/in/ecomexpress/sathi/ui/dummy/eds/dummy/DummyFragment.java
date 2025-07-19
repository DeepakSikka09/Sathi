package in.ecomexpress.sathi.ui.dummy.eds.dummy;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;



import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityDummyBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

/**
 * Created by dhananjayk on 25-01-2019.
 */

@AndroidEntryPoint
public class DummyFragment extends BaseFragment<ActivityDummyBinding, DummyViewModel> implements IDummyNavigation, ActivityData {

    ActivityDummyBinding activityDummyBinding;

    @Inject
    DummyViewModel dummyViewModel;

    public static DummyFragment newInstance() {
        DummyFragment fragment = new DummyFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dummy;
    }

    @Override
    public DummyViewModel getViewModel() {
        return dummyViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dummyViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityDummyBinding = getViewDataBinding();

      //  inItListener();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }





    @Override
    public boolean validateData() {

        return false;
    }

    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {

        return false;
    }

    @Override
    public void setImageValidation() {

    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return null;
    }


    @Override
    public void getData(BaseFragment fragment) {
        boolean activityStatus = false;
/*

        edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
        edsActivityResponseWizard.setInput_value("0");
        edsActivityResponseWizard.setInputRemark("");
        edsActivityResponseWizard.setIsDone("false");
        if (edsActivityWizard.getActualValue().equalsIgnoreCase(amount)) {
            activityStatus = true;
            edsActivityResponseWizard.setInput_value(amount);
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("true");
            edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

        }
        edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
*/
    }

}

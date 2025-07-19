package in.ecomexpress.sathi.ui.dummy.eds.eds_hdfc_masking;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentAmazonBinding;
import in.ecomexpress.sathi.databinding.FragmentHdfcMaskingBinding;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.Constants;

import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;


@AndroidEntryPoint
public class HDFCMaskingFragment extends BaseFragment<FragmentHdfcMaskingBinding, HDFCMaskingFragmentViewModel> implements HDFCMaskingFragmentNavigation {

    FragmentHdfcMaskingBinding fragmentHdfcMaskingBinding;
    EdsWithActivityList edsWithActivityList;
    MasterActivityData masterActivityData;

    @Inject
    HDFCMaskingFragmentViewModel hdfcMaskingFragmentViewModel;

    public static HDFCMaskingFragment newInstance() {

        HDFCMaskingFragment fragment = new HDFCMaskingFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_hdfc_masking;
    }

    @Override
    public HDFCMaskingFragmentViewModel getViewModel() {
        return hdfcMaskingFragmentViewModel;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hdfcMaskingFragmentViewModel.setNavigator(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentHdfcMaskingBinding = getViewDataBinding();
        if (getArguments() != null) {
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void captureFrontImage() {
        EDSDetailActivity.edsDetailActivity.imageHandler.captureImage(hdfcMaskingFragmentViewModel.edsActivityWizard.get().getAwbNo()
                + "_" + edsWithActivityList.edsResponse.getDrsNo()
                + "_" + "EDS_" + hdfcMaskingFragmentViewModel.masterActivityData.get().getCode()
                + "_" + 0 + ".png", fragmentHdfcMaskingBinding.imagecamFront, "EDS_" + hdfcMaskingFragmentViewModel.masterActivityData.get().getCode()
                + "_" + 0, masterActivityData.imageSettings.getWaterMark());
    }

    @Override
    public void captureRearImage() {

    }
}

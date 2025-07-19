package in.ecomexpress.sathi.ui.dummy.eds.eds_amazon;

import android.graphics.Bitmap;
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
import in.ecomexpress.sathi.ui.base.BaseFragment;

@AndroidEntryPoint
public class AmazonFragment extends BaseFragment<FragmentAmazonBinding, AmazonFragmentViewModel> implements AmazonFragmentNavigation {

    FragmentAmazonBinding fragmentAmazonBinding;
    String hash_channal_code;
    String hash_agentID;
    String hash_timeStamp;
    String signature;
    String qr_kyc_string;

    @Inject
    AmazonFragmentViewModel amazonFragmentViewModel;

    public static AmazonFragment newInstance() {

        AmazonFragment fragment = new AmazonFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_amazon;
    }

    @Override
    public AmazonFragmentViewModel getViewModel() {
        return amazonFragmentViewModel;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amazonFragmentViewModel.setNavigator(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentAmazonBinding = getViewDataBinding();
        try {
            try {
                //hash_channal_code = amazonFragmentViewModel.generateHash("channal_code");
                //hash_agentID = amazonFragmentViewModel.generateHash("hash_agentID");
                //hash_timeStamp = amazonFragmentViewModel.generateHash(String.valueOf(System.currentTimeMillis()));
                //signature = hash_agentID + hash_channal_code + hash_timeStamp;
                 qr_kyc_string = "channal_code" + "|" + "agentId" + "|" + "timstamp" + "|" + "signature";
                 amazonFragmentViewModel.generateBitmap(qr_kyc_string, getContext());

                //amazonFragmentViewModel.fadeQRCode(getActivity(), qr_bitmap , fragmentAmazonBinding.qrImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setQRCode(Bitmap bitmap) {
        fragmentAmazonBinding.qrImage.setImageBitmap(bitmap);
        fragmentAmazonBinding.btGetstatus.setVisibility(View.VISIBLE);
    }
}

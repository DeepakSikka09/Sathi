package in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paytmmoneyagent.presentation.qrscannerscreen.QrScannerActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentEkycPaytmBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

@AndroidEntryPoint
public class EdsEkycPaytmFragment extends BaseFragment<FragmentEkycPaytmBinding, EdsEkycPaytmViewModel> implements IEdsEkycPaytmFragmentNavigator, ActivityData {
    FragmentEkycPaytmBinding fragmentEkycPaytmBinding;
    @Inject
    EdsEkycPaytmViewModel edsEkycPaytmViewModel;
    public boolean is_kyc_completed;

    public static EdsEkycPaytmFragment newInstance(){
        EdsEkycPaytmFragment fragment = new EdsEkycPaytmFragment();
        return fragment;
    }

    @Override
    public void getData(BaseFragment fragment){
    }

    @Override
    public boolean validateData(){
        if(is_kyc_completed){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public void validate(boolean flag){
    }

    @Override
    public boolean validateCancelData(){
        return false;
    }

    @Override
    public void setImageValidation(){
    }

    @Override
    public EDSActivityWizard getActivityWizard(){
        return null;
    }

    @Override
    public EdsEkycPaytmViewModel getViewModel(){
        return edsEkycPaytmViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.fragment_ekyc_paytm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        edsEkycPaytmViewModel.setNavigator(this);
        startActivityForResult(new Intent(getContext(), QrScannerActivity.class), 101);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentEkycPaytmBinding = getViewDataBinding();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            String status = data.getExtras().getString("esign_status_code");
            Log.e("status", status);
            if(status.equalsIgnoreCase("VERIFIED")){
                is_kyc_completed = true;
                EDSDetailActivity.edsDetailActivity.callSignatureScreen();
            } else{
                is_kyc_completed = false;
                EDSDetailActivity.edsDetailActivity.cancelRbLScreen();
            }
        } else{
            is_kyc_completed = false;
            EDSDetailActivity.edsDetailActivity.cancelRbLScreen();
        }
    }

    public void showMessage(String s){
        Log.d(TAG, "showMessage: " + s);
    }
}

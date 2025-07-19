package in.ecomexpress.sathi.ui.drs.forward.shipmentearndialog;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;


import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.DialogEarningPerShipmentBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class ShipmentEarnDialog extends BaseDialog implements ShipmentEarnDialogCallBack {

    private static final String TAG = ShipmentEarnDialog.class.getSimpleName();
    @Inject
    ShipmentEarnDialogViewModel shipmentEarnDialogViewModel;
    DialogEarningPerShipmentBinding dialogEarningPerShipmentBinding;
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    static String awb_number;
    static String shipment_type = "";

    public static ShipmentEarnDialog newInstance(Activity getContext, String awb, String type) {
        ShipmentEarnDialog fragment = new ShipmentEarnDialog();
        context = getContext;
        awb_number = awb;
        shipment_type = type;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dialogEarningPerShipmentBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_earning_per_shipment, container, false);
        View view = dialogEarningPerShipmentBinding.getRoot();
        dialogEarningPerShipmentBinding.setViewModel(shipmentEarnDialogViewModel);
        shipmentEarnDialogViewModel.setNavigator(this);
        logScreenNameInGoogleAnalytics(TAG, context);
        setTotalDeliveryAmount();

        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }


    @Override
    public void onDismiss() {
        dismissDialog(TAG);
    }

    @Override
    public void onHandleError(String message) {
        getBaseActivity().showSnackbar(message);
    }


    @SuppressLint("SetTextI18n")
    public void setTotalDeliveryAmount() {
        try {
            if (shipment_type.equalsIgnoreCase("PPD")) {
                dialogEarningPerShipmentBinding.imgView.setImageResource(R.drawable.rupeesymbol);
                dialogEarningPerShipmentBinding.oneShpEarn.setText("₹ " + shipmentEarnDialogViewModel.getDataManager().getPPDPrice());
                dialogEarningPerShipmentBinding.awb.setText(awb_number);
            } else if (shipment_type.equalsIgnoreCase("COD")) {
                dialogEarningPerShipmentBinding.imgView.setImageResource(R.drawable.rupeesymbol);
                dialogEarningPerShipmentBinding.oneShpEarn.setText("₹ " + shipmentEarnDialogViewModel.getDataManager().getCODPrice());
                dialogEarningPerShipmentBinding.awb.setText(awb_number);
            } else if (shipment_type.equalsIgnoreCase("RQC")) {
                dialogEarningPerShipmentBinding.imgView.setImageResource(R.drawable.rupeesymbol);
                dialogEarningPerShipmentBinding.oneShpEarn.setText("₹ " + shipmentEarnDialogViewModel.getDataManager().getRQCPrice());
                dialogEarningPerShipmentBinding.awb.setText(awb_number);
            } else if (shipment_type.equalsIgnoreCase("RVP")) {
                dialogEarningPerShipmentBinding.imgView.setImageResource(R.drawable.rupeesymbol);
                dialogEarningPerShipmentBinding.oneShpEarn.setText("₹ " + shipmentEarnDialogViewModel.getDataManager().getRVPPrice());
                dialogEarningPerShipmentBinding.awb.setText(awb_number);
            } else {
                dialogEarningPerShipmentBinding.imgView.setImageResource(R.drawable.rupeesymbol);
                dialogEarningPerShipmentBinding.oneShpEarn.setText("₹ " + shipmentEarnDialogViewModel.getDataManager().getEDSPrice());
                dialogEarningPerShipmentBinding.awb.setText(awb_number);
            }

        } catch (Exception e) {
            Logger.e(ShipmentEarnDialog.class.getName(), e.getMessage());

            getBaseActivity().showSnackbar(e.getMessage());
        }
    }


}
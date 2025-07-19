package in.ecomexpress.sathi.ui.drs.rts.rts_main_list;

import static in.ecomexpress.sathi.SathiApplication.shipmentImageMap;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.ActivityRtsShipmentListItemBinding;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.ui.drs.rts.interfaces.AdapterCheckBoxCallBack;
import in.ecomexpress.sathi.ui.drs.rts.interfaces.ClickListener;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

public class RTSShipmentListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {

    private final ItemFilter mFilter = new ItemFilter();
    private final String TAG = RTSShipmentListAdapter.class.getSimpleName();
    List<ShipmentsDetail> shipmentsDetailList;
    List<ShipmentsDetail> filterShipments = new ArrayList<>();
    boolean isFromScanner = false;
    boolean isCheckBoxVisible;
    AdapterCheckBoxCallBack adapterCheckBoxCallBack;
    RTSListActivityViewModel rtsListActivityViewModel;
    private HashMap<Integer, RTSReasonCodeMaster> mapRTSReasonCodeMaster;
    private ClickListener clickListener;
    private IRTSAdapterInterface itsAdapterListener;
    private final Map<Long, String> manuallyEnteredFlyerCodes = new HashMap<>();
    ActivityRtsShipmentListItemBinding activityRtsShipmentListItemBinding;
    ShipmentsDetail commonDRSListItems;

    public RTSShipmentListAdapter(List<ShipmentsDetail> shipmentsDetailList, boolean isCheckBoxVisible) {
        this.shipmentsDetailList = shipmentsDetailList;
        filterShipments.clear();
        manuallyEnteredFlyerCodes.clear();
        this.isCheckBoxVisible = isCheckBoxVisible;
        filterShipments.addAll(shipmentsDetailList);
        Collections.sort(filterShipments);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setRTSReasonCodeMaster(HashMap<Integer, RTSReasonCodeMaster> mapRTSReasonCodeMaster) {
        this.mapRTSReasonCodeMaster = mapRTSReasonCodeMaster;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ShipmentsDetail> shipmentsDetailList, ClickListener clickListener) {
        if(!clickListener.toString().contains("RTSScanActivity")){
            this.shipmentsDetailList = shipmentsDetailList;
            this.filterShipments.clear();
            this.clickListener = clickListener;
            this.filterShipments.addAll(shipmentsDetailList);
            Collections.sort(filterShipments);
            isFromScanner = false;
            notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(HashSet<ShipmentsDetail> shipmentsDetailList, boolean isCheckBoxVisible, ShipmentsDetail commonDRSListItems, AdapterCheckBoxCallBack adapterCheckBoxCallBack) {
        isFromScanner = true;
        filterShipments.clear();
        this.commonDRSListItems = commonDRSListItems;
        this.isCheckBoxVisible = isCheckBoxVisible;
        this.filterShipments.addAll(shipmentsDetailList);
        this.adapterCheckBoxCallBack = adapterCheckBoxCallBack;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityRtsShipmentListItemBinding = ActivityRtsShipmentListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityRtsShipmentListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return filterShipments.size();
    }

    public void setUpdateStaticListener(IRTSAdapterInterface itUpdateStaticListener) {
        this.itsAdapterListener = itUpdateStaticListener;
    }

    public void updateDRS(HashMap<Long, Bitmap> shipment_image_map) {
        shipmentImageMap = shipment_image_map;
    }

    void setShipmentImagesVisibility(ShipmentsDetail shipmentsDetail, ActivityRtsShipmentListItemBinding mBinding) {
        if (shipmentsDetail.getIMAGEM()) {
            mBinding.disputedImage.setVisibility(View.VISIBLE);
            if (shipmentsDetail.isIS_IMAGE_CAPTURED()) {
                mBinding.disputedImage.setImageBitmap(shipmentImageMap.get(shipmentsDetail.getAwbNo()));
            } else {
                mBinding.disputedImage.setImageResource(R.drawable.cam);
            }
        } else {
            mBinding.disputedImage.setVisibility(View.GONE);
        }
    }

    public List<ShipmentsDetail> getList() {
        return filterShipments;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<ShipmentsDetail> list = shipmentsDetailList;
            int count = list.size();
            final ArrayList<ShipmentsDetail> nList = new ArrayList<>(count);
            String filterableString;
            String filterableStringParent;
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getAwbNo() + "";
                filterableStringParent = list.get(i).getParentAwbNo();
                if (filterableString.toLowerCase().contains(filterString) || filterableStringParent.toLowerCase().contains(filterString)) {
                    nList.add(list.get(i));
                }
            }
            results.values = nList;
            results.count = nList.size();
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterShipments.clear();
            filterShipments.addAll((List<ShipmentsDetail>) results.values);
            Collections.sort(filterShipments);
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends BaseViewHolder {
        ActivityRtsShipmentListItemBinding mBinding;
        RTSShipmentViewModel rtsShipmentViewModel;

        public MyViewHolder(ActivityRtsShipmentListItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public void onBind(int position) {
            try{
                ShipmentsDetail shipmentsDetail = filterShipments.get(position);
                rtsShipmentViewModel = new RTSShipmentViewModel(shipmentsDetail);
                mBinding.setViewModel(rtsShipmentViewModel);
                mBinding.executePendingBindings();
                if (isCheckBoxVisible) {
                    mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                }
                mBinding.checkboxCkb.setChecked(shipmentsDetail.isChecked());
                mBinding.checkboxCkb.setTag(position);
                mBinding.disputedImage.setTag(position);
                try {
                    if (shipmentsDetail.getReasonCode() == 999) {
                        mBinding.statusTv.setText(shipmentsDetail.getStatus() + (Objects.requireNonNull(mapRTSReasonCodeMaster.get(shipmentsDetail.getReasonCode())).getReasonMessage() != null ? (":" + Objects.requireNonNull(mapRTSReasonCodeMaster.get(shipmentsDetail.getReasonCode())).getReasonMessage()) : ""));
                    } else {
                        mBinding.statusTv.setText(shipmentsDetail.getStatus());
                    }
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                if (shipmentsDetail.getStatus() != null) {
                    switch (shipmentsDetail.getStatus()) {
                        case Constants.RTSDELIVERED:
                        case Constants.RTSDELIVEREDBbutDAMAGED:
                        case Constants.RTSMANUALLYDELIVERED:
                        case Constants.RTSMANUALLYDELIVEREDbutDAMAGED:
                            mBinding.disputedImage.setVisibility(View.GONE);
                            mBinding.shipmentBackground.setBackgroundResource(R.drawable.rts_delivered_gradient);
                            break;
                        case Constants.RTSASSIGNED:
                            mBinding.disputedImage.setVisibility(View.GONE);
                            mBinding.greenTick.setVisibility(View.GONE);
                            mBinding.shipmentBackground.setBackgroundResource(R.drawable.rounded_background);
                            break;
                        case Constants.RTSUNDELIVERED:
                            mBinding.greenTick.setVisibility(View.GONE);
                            setShipmentImagesVisibility(shipmentsDetail, mBinding);
                            mBinding.shipmentBackground.setBackgroundResource(R.drawable.rts_undelivered_gradient);
                            break;
                        case Constants.RTSDISPUTED:
                            mBinding.greenTick.setVisibility(View.GONE);
                            setShipmentImagesVisibility(shipmentsDetail, mBinding);
                            mBinding.shipmentBackground.setBackgroundResource(R.drawable.rts_delivered_gradient);
                            break;
                        default:
                            mBinding.shipmentBackground.setBackgroundResource(R.drawable.rounded_background);
                            break;
                    }
                    if (shipmentsDetail.getStatus().contains(Constants.RTSUNDELIVERED)) {
                        mBinding.shipmentBackground.setBackgroundResource(R.drawable.rts_undelivered_gradient);
                    }
                    if (!shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.checkboxCkb.setChecked(false);
                    }
                    if (shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                        if (isCheckBoxVisible) {
                            mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                            mBinding.checkboxCkb.setChecked(shipmentsDetail.isChecked());
                        }
                    }
                }
                // Check recycler is from scanner if yes then perform the task.
                try{
                    if(isFromScanner){
                        if (shipmentsDetail.isIs_flyer_scanned()) {
                            mBinding.flyerError.setVisibility(View.GONE);
                        }
                        else {
                            if (commonDRSListItems != null && commonDRSListItems.getReturn_package_barcode().equalsIgnoreCase("")) {
                                commonDRSListItems.setIs_flyer_scanned(true);
                            }
                            else {
                                mBinding.flyerError.setVisibility(View.VISIBLE);
                                String errorMessage = commonDRSListItems.getParentAwbNo().equalsIgnoreCase("") ? "Scan Flyer Code" : "Scan Branded Barcode";
                                mBinding.flyerError.setText(errorMessage);
                            }
                        }
                        // Show check box only if shipment type is RVP, getFlyerMismatch false and those awb whose flyer scan is pending:-
                        if (!shipmentsDetail.getReturn_package_barcode().equalsIgnoreCase("") && !shipmentsDetail.isIs_flyer_scanned()){
                            mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                            mBinding.checkboxCkb.setChecked(false);
                        }
                        else{
                            mBinding.checkboxCkb.setVisibility(View.GONE);
                        }
                        // Show the manually input flyer barcode value in UI:-
                        Long currentAwbNumber = filterShipments.get(position).getAwbNo();
                        if (manuallyEnteredFlyerCodes.containsKey(currentAwbNumber)) {
                            mBinding.manuallyFlyerInput.setVisibility(View.VISIBLE);
                            mBinding.manuallyFlyerInput.setText(manuallyEnteredFlyerCodes.get(currentAwbNumber));
                        }
                        else {
                            mBinding.manuallyFlyerInput.setVisibility(View.GONE);
                        }

                        /*
                         * RTSDELIVERED : Check shipment status.
                         * rtsDeliveredImage: Check capture two image flag true or false.
                         * capturedImageCount : Check FE capture both image or not.
                         * */
                        if(shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSDELIVERED) && CommonUtils.getRtsDeliveredImagesValue(shipmentsDetail.getFlagsMap())){
                            workForTwoImage(true, true, mBinding, CommonUtils.capturedImageCount(shipmentsDetail.getAwbNo()));
                        }
                    }
                }
                catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }

                // Check shipment is manually marked delivered or not.
                if((shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSMANUALLYDELIVERED) || shipmentsDetail.getStatus().equalsIgnoreCase(Constants.RTSDELIVERED)) && CommonUtils.getRtsDeliveredImagesValue(shipmentsDetail.getFlagsMap())){
                    workForTwoImage(true, true, mBinding, CommonUtils.capturedImageCount(shipmentsDetail.getAwbNo()));
                }

                mBinding.checkboxCkb.setOnClickListener(v -> {
                    if(isFromScanner) {
                        adapterCheckBoxCallBack.onCheckBoxClick();
                    }
                    else {
                        int selectedItemPosition = Integer.parseInt(v.getTag().toString());
                        shipmentsDetail.setChecked(!filterShipments.get(selectedItemPosition).isChecked());
                        notifyItemChanged(selectedItemPosition);
                        // Updating the check box state present in the RTSListActivity:-
                        rtsListActivityViewModel.getNavigator().setFWDCheckBoxStatus(rtsListActivityViewModel.getFWDPacketCount() != 0 && (rtsListActivityViewModel.getFWDPacketCount() == rtsListActivityViewModel.getFWDCheckedCount()));
                        rtsListActivityViewModel.getNavigator().setRVPCheckBoxStatus(rtsListActivityViewModel.getRVPPacketCount() != 0 && (rtsListActivityViewModel.getRVPPacketCount() == rtsListActivityViewModel.getRVPCheckedCount()));
                    }
                });
                mBinding.shipmentBackground.setOnLongClickListener(v -> {
                    if (!shipmentsDetail.getStatus().contains(Constants.RTSASSIGNED)) {
                        new AlertDialog.Builder(mBinding.getRoot().getContext()).setMessage("Do you want to mark this shipment again assigned ?").setPositiveButton("Assign", (dialog, which) -> {
                            shipmentsDetail.setStatus(Constants.RTSASSIGNED);
                            shipmentsDetail.setChecked(false);
                            shipmentsDetail.setReasonCode(0);
                            shipmentsDetail.setIS_FLYER_WRONG_CAPTURED(false);
                            SathiApplication.rtsCapturedImage1.put(shipmentsDetail.getAwbNo(), false);
                            SathiApplication.rtsCapturedImage2.put(shipmentsDetail.getAwbNo(), false);
                            itsAdapterListener.update(shipmentsDetail);
                            notifyDataSetChanged();
                        }).setNegativeButton("Cancel", null).show();
                    }
                    return false;
                });
                mBinding.disputedImage.setOnClickListener(v -> clickListener.onCameraClick(mBinding.disputedImage, shipmentsDetail.getAwbNo(), getAdapterPosition(), shipmentsDetail.getStatus()));
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    public void workForTwoImage(boolean isRtsDelivered,boolean isRtsDeliveredImageTrue , ActivityRtsShipmentListItemBinding mBinding, int imageCount){
        // Check if shipment status is Delivered and flag is true:-
        if (isRtsDelivered && isRtsDeliveredImageTrue) {
            // Check if FE click both image or not.
            if (imageCount < 2) {
                mBinding.greenTick.setVisibility(View.GONE);
                mBinding.disputedImage.setVisibility(View.VISIBLE);
            } else {
                mBinding.greenTick.setVisibility(View.VISIBLE);
                mBinding.disputedImage.setVisibility(View.GONE);
            }
        } else {
            mBinding.disputedImage.setVisibility(View.GONE);
            mBinding.greenTick.setVisibility(View.GONE);
        }
    }

    public void setRTSListActivityViewModelInstance(RTSListActivityViewModel RTSListActivityViewModel) {
       this.rtsListActivityViewModel = RTSListActivityViewModel;
    }

    public void setClickListenerInstance(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void manuallyEnteredFlyerCode(String manuallyEnteredFlyerCodeValue, Long manuallyEnteredFlyerCodeAWB) {
        manuallyEnteredFlyerCodes.put(manuallyEnteredFlyerCodeAWB, manuallyEnteredFlyerCodeValue);
        notifyDataSetChanged();
    }
}
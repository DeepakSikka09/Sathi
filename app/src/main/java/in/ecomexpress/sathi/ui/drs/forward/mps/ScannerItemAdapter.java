package in.ecomexpress.sathi.ui.drs.forward.mps;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityForwardScanSingleItemBinding;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.utils.Logger;



public class ScannerItemAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private final String TAG = ScannerItemAdapter.class.getSimpleName();

    private List<MPSShipment> mpsShipments = new ArrayList<>();

    public void setData(List<MPSShipment> awbList) {
        this.mpsShipments.clear();
        this.mpsShipments.addAll(awbList);
        notifyDataSetChanged();
    }


    public ScannerItemAdapter(List<MPSShipment> awbList) {
        this.mpsShipments = awbList;
    }

    public void clear() {
        mpsShipments.clear();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityForwardScanSingleItemBinding activityForwardScanSingleItemBinding = ActivityForwardScanSingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MyViewHolder(activityForwardScanSingleItemBinding);


    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mpsShipments.size();
    }

    class MyViewHolder extends BaseViewHolder {

        ActivityForwardScanSingleItemBinding mBinding;
        ScannerItemAdapterViewModel scannerItemAdapterViewModel;

        public MyViewHolder(ActivityForwardScanSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }


        @Override
        public void onBind(int position) {
            Logger.e(TAG, "position: " + position);
            MPSShipment mpsShipment = mpsShipments.get(position);
            scannerItemAdapterViewModel = new ScannerItemAdapterViewModel(mpsShipment.scanned);
            mBinding.setViewModel(scannerItemAdapterViewModel);

            mBinding.executePendingBindings();

            mBinding.textViewBarcode1.setText("Please scan AWB No. " + mpsShipment.awbNo);

            if (mpsShipment.scanned) {
                mBinding.imgVwStatusIndicator2.setBackgroundResource(R.drawable.tick);
                mBinding.textViewBarcode1.setText("Scanned AWB No. " + mpsShipment.awbNo);
            } else {
                mBinding.imgVwStatusIndicator2.setBackgroundResource(R.drawable.cross);
            }

            mBinding.imgScanBfPkg.setTag(position);

        }
    }

}





package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.AdapterPendingHistoryBinding;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.utils.Logger;

public class PendingHistoryAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<PushApi> pendingHistories;
    private Context context;
    AdapterPendingHistoryBinding mItemBinding;

    public PendingHistoryAdapter(List<PushApi> list){
        this.pendingHistories = list;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(Context context, List<PushApi> list){
        this.pendingHistories.clear();
        this.pendingHistories.addAll(list);
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        AdapterPendingHistoryBinding mBinding = AdapterPendingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(mBinding);
    }

    class MyViewHolder extends BaseViewHolder {
        AdapterPendingHistoryBinding itemBinding;

        MyViewHolder(AdapterPendingHistoryBinding mBinding){
            super(mBinding.getRoot());
            itemBinding = mBinding;
            mItemBinding = mBinding;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBind(int position){
            try{
                if(pendingHistories.get(position).getShipmentCaterogy().equalsIgnoreCase("RTS")) {
                    itemBinding.tvAwbno.setText("Seller Name:- " + pendingHistories.get(position).vendor_name);
                    itemBinding.tvShipmentType.setText("Shipment Type : " + pendingHistories.get(position).getShipmentCaterogy());
                } else {
                    itemBinding.tvAwbno.setText("AWB Number : " + pendingHistories.get(position).getAwbNo());
                    itemBinding.tvShipmentType.setText("Shipment Type : " + pendingHistories.get(position).getShipmentCaterogy());
                }
                if(pendingHistories.get(position).getShipmentStatus() != 2){
                    itemBinding.tvStatus.setText("NOT SYNCED");
                    itemBinding.tvStatus.setTextColor(context.getResources().getColor(R.color.red_ecom));
                } else{
                    itemBinding.tvStatus.setText("SYNCED");
                    itemBinding.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                }
            } catch(Exception e){
                Logger.e("PendingHistoryAdapter", String.valueOf(e));
            }
            itemBinding.parentCardview.setOnClickListener(view -> {
                Intent pendingHistoryDetailActivity = new Intent(context, PendingHistoryDetailActivity.class);
                pendingHistoryDetailActivity.putExtra("awb_no", pendingHistories.get(position).getAwbNo());
                pendingHistoryDetailActivity.putExtra("drs_id", pendingHistories.get(position).getAwbNo());
                pendingHistoryDetailActivity.putExtra("shipment_type", pendingHistories.get(position).getShipmentCaterogy());
                pendingHistoryDetailActivity.putExtra("composite_key", pendingHistories.get(position).CompositeKey);
                pendingHistoryDetailActivity.putExtra("vendor_name", pendingHistories.get(position).vendor_name);
                context.startActivity(pendingHistoryDetailActivity);
                applyTransitionToBackFromActivity(((Activity) context));
            });
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        holder.onBind(position);
    }

    @Override
    public int getItemCount(){
        return pendingHistories.size();
    }
}
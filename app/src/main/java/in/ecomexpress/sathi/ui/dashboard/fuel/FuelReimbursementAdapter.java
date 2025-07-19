package in.ecomexpress.sathi.ui.dashboard.fuel;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.databinding.ActivityFuelListItemsBinding;
import in.ecomexpress.sathi.repo.remote.model.fuel.response.Reports;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

public class FuelReimbursementAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final List<Reports> myreportsList;

    public FuelReimbursementAdapter(List<Reports> reportsList){
        this.myreportsList = reportsList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Reports> reportsList){
        this.myreportsList.clear();
        this.myreportsList.addAll(reportsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ActivityFuelListItemsBinding activityFuelListItemsBinding = ActivityFuelListItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityFuelListItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        holder.onBind(position);
    }

    @Override
    public int getItemCount(){
        return myreportsList.size();//50
    }

    private class MyViewHolder extends BaseViewHolder {
        ActivityFuelListItemsBinding mBinding;
        FuelReimburseItemViewModel fuelReimburseItemViewModel;

        public MyViewHolder(ActivityFuelListItemsBinding binding){
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position){
            Reports fuelReimbursementResponse = myreportsList.get(position);
            fuelReimburseItemViewModel = new FuelReimburseItemViewModel(fuelReimbursementResponse);
            mBinding.setViewModel(fuelReimburseItemViewModel);
            mBinding.executePendingBindings();
            mBinding.map.setOnClickListener(v -> {});
        }
    }
}
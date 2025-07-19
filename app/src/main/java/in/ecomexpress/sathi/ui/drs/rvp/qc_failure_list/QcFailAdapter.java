package in.ecomexpress.sathi.ui.drs.rvp.qc_failure_list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityQcFailSingleItemBinding;
import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

/**
 * Created by shivangis on 9/6/2018.
 */

public class QcFailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<RvpCommit.QcWizard> myqcWizards;

    public void setData(List<RvpCommit.QcWizard> qcWizardList) {
        this.myqcWizards.clear();
        this.myqcWizards.addAll(qcWizardList);
        notifyDataSetChanged();
    }

    public QcFailAdapter(List<RvpCommit.QcWizard> reportsList) {
        this.myqcWizards = reportsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityQcFailSingleItemBinding activityFuelListItemsBinding = ActivityQcFailSingleItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityFuelListItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myqcWizards.size();
    }

    private class MyViewHolder extends BaseViewHolder {
        ActivityQcFailSingleItemBinding mBinding;
        QcFailItemViewModel QcFailItemViewModel;

        public MyViewHolder(ActivityQcFailSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            RvpCommit.QcWizard wizard = myqcWizards.get(position);
            QcFailItemViewModel = new QcFailItemViewModel(wizard.getQcName(), wizard.getMatch().equalsIgnoreCase("1") ? "True" : "False", "M");
            if (wizard.getMatch().equals("1")) {
                mBinding.failLayout.setBackgroundResource(R.drawable.delivered_gradient);
            } else {
                mBinding.failLayout.setBackgroundResource(R.drawable.undelivered_gradient);
            }
            mBinding.setViewModel(QcFailItemViewModel);
            mBinding.executePendingBindings();
        }
    }
}


package in.ecomexpress.sathi.ui.dashboard.switchnumber;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivitySwitchnumbersinglelistitemBinding;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

public class SwitchNumberListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final List<CbPstnOptions> mycbPstnOptions;
    String myPstnFormat;
    int mLastSelectedIndex = -1;
    boolean isItemClicked = false;
    SwitchNumberViewModel switchNumberViewModel;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CbPstnOptions> cbPstnOptions, SwitchNumberViewModel switchNumberViewModel){
        this.mycbPstnOptions.clear();
        for (CbPstnOptions option : cbPstnOptions) {
            if (option.getPstn_format().contains("KALEYRA")) {
                this.mycbPstnOptions.add(option);
            }
        }
        this.switchNumberViewModel = switchNumberViewModel;
        notifyDataSetChanged();
    }

    public SwitchNumberListAdapter(List<CbPstnOptions> cbPstnOptions){
        this.mycbPstnOptions = cbPstnOptions;
    }

    public String getPstnFormat(){
        return myPstnFormat;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ActivitySwitchnumbersinglelistitemBinding activitySwitchnumbersinglelistitemBinding = ActivitySwitchnumbersinglelistitemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activitySwitchnumbersinglelistitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        holder.onBind(position);
    }

    @Override
    public int getItemCount(){
        return mycbPstnOptions.size();//50
    }

    private class MyViewHolder extends BaseViewHolder implements ItemListener {

        ActivitySwitchnumbersinglelistitemBinding mBinding;
        SwitchNumberItemViewModel switchNumberItemViewModel;

        public MyViewHolder(ActivitySwitchnumbersinglelistitemBinding binding){
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position){
            CbPstnOptions cbPstnOptions = mycbPstnOptions.get(position);
            switchNumberItemViewModel = new SwitchNumberItemViewModel(cbPstnOptions, this);
            mBinding.setViewModel(switchNumberItemViewModel);
            mBinding.executePendingBindings();
            if(!isItemClicked && cbPstnOptions.getPstn_format().equalsIgnoreCase(switchNumberViewModel.getDataManager().getPstnFormat())){
                mBinding.popupElement.setBackgroundResource(R.drawable.delivered_gradient);
                myPstnFormat = switchNumberViewModel.getDataManager().getPstnFormat();
            } else if(isItemClicked && mLastSelectedIndex == position){
                mBinding.popupElement.setBackgroundResource(R.drawable.delivered_gradient);
            } else{
                mBinding.popupElement.setBackgroundResource(R.color.offwhite);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onItemClick(CbPstnOptions cbPstnOptions){
            mLastSelectedIndex = getAdapterPosition();
            myPstnFormat = cbPstnOptions.getPstn_format();
            isItemClicked = true;
            notifyDataSetChanged();
        }
    }
}
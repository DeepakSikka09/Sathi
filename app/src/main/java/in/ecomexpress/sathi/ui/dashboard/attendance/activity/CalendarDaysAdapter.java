package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import in.ecomexpress.sathi.databinding.ActivityDaysSingleItemBinding;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

public class CalendarDaysAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final ArrayList<String> mydayList;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ArrayList<String> daylist) {
        this.mydayList.clear();
        this.mydayList.addAll(daylist);
        notifyDataSetChanged();
    }

    public CalendarDaysAdapter(ArrayList<String> mydayList) {
        this.mydayList = mydayList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityDaysSingleItemBinding activityDaysSingleItemBinding = ActivityDaysSingleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityDaysSingleItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mydayList.size();
    }


    private class MyViewHolder extends BaseViewHolder {

        ActivityDaysSingleItemBinding mBinding;
        CalendarDaysViewModel calendarDaysViewModel;

        public MyViewHolder(ActivityDaysSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            calendarDaysViewModel = new CalendarDaysViewModel(mydayList.get(position));
            mBinding.dayName.setText(calendarDaysViewModel.getDay());
            mBinding.setViewModel(calendarDaysViewModel);
            mBinding.executePendingBindings();
        }
    }
}
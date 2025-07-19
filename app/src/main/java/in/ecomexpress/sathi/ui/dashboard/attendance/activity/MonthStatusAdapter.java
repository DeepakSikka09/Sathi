package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityAttendanceStatusSingleItemBinding;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponseList;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

public class MonthStatusAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<AttendanceResponseList> myattendanceResponseLists;
    AttendanceViewModel attendanceViewModel;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<AttendanceResponseList> attendanceResponseLists, AttendanceViewModel attendanceViewModel) {
        this.myattendanceResponseLists.clear();
        this.attendanceViewModel = attendanceViewModel;
        this.myattendanceResponseLists.addAll(attendanceResponseLists);
        notifyDataSetChanged();
    }

    public MonthStatusAdapter(List<AttendanceResponseList> attendanceResponseLists) {
        this.myattendanceResponseLists = attendanceResponseLists;
    }

    public void clear() {
        myattendanceResponseLists.clear();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityAttendanceStatusSingleItemBinding activityAttendanceStatusSingleItemBinding = ActivityAttendanceStatusSingleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityAttendanceStatusSingleItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return myattendanceResponseLists.size();
    }

    private class MyViewHolder extends BaseViewHolder {

        ActivityAttendanceStatusSingleItemBinding mBinding;
        MonthStatusItemViewModel monthStatusItemViewModel;

        public MyViewHolder(ActivityAttendanceStatusSingleItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            AttendanceResponseList attendanceResponseList = myattendanceResponseLists.get(position);
            monthStatusItemViewModel = new MonthStatusItemViewModel(attendanceResponseList);
            mBinding.setViewModel(monthStatusItemViewModel);
            try {
                if (myattendanceResponseLists.get(position).getAttendance_status().equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.P))) {
                    mBinding.attendanceStatus.setTextColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.green_text));
                }
                else if (myattendanceResponseLists.get(position).getAttendance_status().equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.A))){
                    mBinding.attendanceStatus.setTextColor(Color.RED);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mBinding.executePendingBindings();
            if (!myattendanceResponseLists.get(position).getAttendance_status().isEmpty() && myattendanceResponseLists.get(position).getAttendance_status() != null) {
                mBinding.card.setOnClickListener(view -> {
                    String Sdate = monthStatusItemViewModel.FormattedDate();
                    String Sintime = monthStatusItemViewModel.Intime();
                    String Souttime = monthStatusItemViewModel.outTime();
                    String SworkingHours = monthStatusItemViewModel.WorkingHours();
                    String Sstatus = monthStatusItemViewModel.status();
                    attendanceViewModel.getSingleDayData(Sdate, Sintime, Souttime, SworkingHours, Sstatus);
                });
            }
        }
    }
}
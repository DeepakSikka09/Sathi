package in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityMonthItemviewBinding;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;

public class CustomDialogAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final ArrayList<String> mymonthList;
    int currentMonth;
    int tempCurrentMonth;
    public int selectedMonth = -1;
    String getSelectedMonth = null;
    int year;
    int currentYear;

    public void setData(ArrayList<String> monthlist) {
        this.mymonthList.clear();
        this.mymonthList.addAll(monthlist);
        notifyDataSetChanged();
    }

    public CustomDialogAdapter(ArrayList<String> mymonthList) {
        this.mymonthList = mymonthList;
        final Calendar c = Calendar.getInstance();
        currentMonth = c.get(Calendar.MONTH);
        currentYear = c.get(Calendar.YEAR);
        tempCurrentMonth = currentMonth;
    }

    public void getYear(int year) {
        this.year = year;
        selectedMonth = -1;
        notifyDataSetChanged();
    }


    public String getMonth() {
        return getSelectedMonth;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ActivityMonthItemviewBinding activityMonthItemviewBinding = ActivityMonthItemviewBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(activityMonthItemviewBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        return mymonthList.size();
    }


    private class MyViewHolder extends BaseViewHolder implements ICustomDialogAdapterInterface {
        ActivityMonthItemviewBinding mBinding;
        CustomDialogItemViewModel customDialogItemViewModel;

        public MyViewHolder(ActivityMonthItemviewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {

            customDialogItemViewModel = new CustomDialogItemViewModel(mymonthList.get(position), this);
            mBinding.monthName.setText(customDialogItemViewModel.getName());

            mBinding.setViewModel(customDialogItemViewModel);
            mBinding.executePendingBindings();
            tempCurrentMonth = currentMonth;


            if (currentMonth == 0) {
                if (year == currentYear) {
                    if (position == 0) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                } else {
                    if (position == 11 || position == 10 || position == 9) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                }
                if (selectedMonth == position) {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_code_background);
                } else {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_keyboard_background);
                }

            }

            if (currentMonth == 1) {
                if (year == currentYear) {
                    if (position == 1 || position == 0) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                } else {
                    if (position == 11 || position == 10) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                }
                if (selectedMonth == position) {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_code_background);
                } else {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_keyboard_background);
                }
            }

            if (currentMonth == 2) {
                if (year == currentYear) {
                    if (position == 2 || position == 1 || position == 0) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                } else {
                    if (position == 11) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else
                        mBinding.monthName.setTextColor(Color.GRAY);
                }
                if (selectedMonth == position) {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_code_background);
                } else {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_keyboard_background);
                }
            }
            if (currentMonth > 2) {
                if (year == currentYear) {
                    if (position == currentMonth || position == tempCurrentMonth - 1 || position == tempCurrentMonth - 2 || position == tempCurrentMonth - 3) {
                        mBinding.monthName.setTextColor(Color.BLACK);
                    } else {
                        mBinding.monthName.setTextColor(Color.GRAY);
                    }
                }
                if (selectedMonth == position) {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_code_background);
                } else {
                    mBinding.layoutMonth.setBackgroundResource(R.drawable.pin_keyboard_background);
                }
            }
            mBinding.monthName.setTag(position);
            mBinding.monthName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mBinding.getRoot().getContext());
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.custom_dialog);

                    boolean enableView = false;
                    int selectedPosition = Integer.parseInt(view.getTag().toString());
                    if (currentMonth == 0) {
                        if (year == currentYear) {
                            if (position == 0) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (position == 11 || position == 10 || position == 9) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (currentMonth == 1) {
                        if (year == currentYear) {
                            if (position == 1 || position == 0) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (position == 11 || position == 10) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (currentMonth == 2) {
                        if (year == currentYear) {
                            if (position == 2 || position == 1 || position == 0) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (position == 11) {
                                getSelectedMonth = mymonthList.get(selectedPosition);
                                enableView = true;
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (currentMonth > 2) {
                        if (position == currentMonth || position == tempCurrentMonth - 1 || position == tempCurrentMonth - 2 || position == tempCurrentMonth - 3) {
                            getSelectedMonth = mymonthList.get(selectedPosition);
                            enableView = true;
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), R.string.month_validation_msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                    if (enableView) {

                        selectedMonth = selectedPosition;
                        notifyDataSetChanged();
                    }
                }
            });
        }


        @Override
        public void onItemClick() {
        }
    }

}

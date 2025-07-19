package in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog;

public class CustomDialogItemViewModel {
    public String monthName;
    public ICustomDialogAdapterInterface customDialogAdapterInterface;

    public CustomDialogItemViewModel(String monthName, ICustomDialogAdapterInterface customDialogAdapterInterface) {
        this.monthName = monthName;
        this.customDialogAdapterInterface = customDialogAdapterInterface;
    }

    public String getName() {
        return monthName;
    }
}

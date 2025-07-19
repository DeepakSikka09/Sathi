package in.ecomexpress.sathi.ui.drs.forward.obd.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDQcPassActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.OBDFragment;
import in.ecomexpress.sathi.ui.drs.forward.obd.viewmodel.FwdOBDcCPassViewModel;

public class OBDPagerAdapter extends FragmentStateAdapter {

    FwdOBDcCPassViewModel fwdOBDcCPassViewModel;
    private final int qcCompletedCount;
    private final FwdOBDQcPassActivity fwdOBDQcPassActivity;

    public OBDPagerAdapter(int qcCompletedCount, FwdOBDQcPassActivity fwdOBDQcPassActivity, FwdOBDcCPassViewModel fwdOBDcCPassViewModel) {
        super(fwdOBDQcPassActivity);
        this.qcCompletedCount = qcCompletedCount;
        this.fwdOBDQcPassActivity = fwdOBDQcPassActivity;
        this.fwdOBDcCPassViewModel = fwdOBDcCPassViewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return new OBDFragment(fwdOBDcCPassViewModel.qualityCheckName.get(qcCompletedCount), qcCompletedCount, fwdOBDQcPassActivity, fwdOBDcCPassViewModel);
    }

    @Override
    public int getItemCount() {
        return fwdOBDcCPassViewModel.numberOfQualityChecks;
    }
}

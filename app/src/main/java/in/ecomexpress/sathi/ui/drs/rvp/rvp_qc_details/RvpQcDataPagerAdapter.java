package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;
import java.util.Objects;

import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check.QcCheckFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check_image.QcCheckImageFragment;
import in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input.QcInputFragment;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

public class RvpQcDataPagerAdapter extends FragmentStatePagerAdapter {
    RvpWithQC rvpWithQCS;
    List<SampleQuestion> sampleQuestions;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public RvpQcDataPagerAdapter(FragmentManager fragmentManager, RvpWithQC rvpWithQCS, List<SampleQuestion> sampleQues) {
        super(fragmentManager);
        this.rvpWithQCS = rvpWithQCS;
        this.sampleQuestions = sampleQues;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        SampleQuestion sampleQuestion = getCurrentSampleQuestion(rvpWithQCS.rvpQualityCheckList.get(position).getQcCode());
        String type = Objects.requireNonNull(sampleQuestion).getVerificationMode();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.QC_CHECK_LIST, rvpWithQCS.rvpQualityCheckList.get(position));
        bundle.putParcelable(Constants.QC_SAMPLE_QC, sampleQuestion);
        if (GlobalConstant.QcTypeConstants.CHECK.equalsIgnoreCase(type)) {
            QcCheckFragment qcCheckFragment = QcCheckFragment.newInstance();
            qcCheckFragment.setArguments(bundle);
            return qcCheckFragment;
        } else if (type.contains(GlobalConstant.QcTypeConstants.INPUT)) {
            QcInputFragment qcInputFragment = QcInputFragment.newInstance();
            qcInputFragment.setArguments(bundle);
            return qcInputFragment;
        } else if (GlobalConstant.QcTypeConstants.CHECK_IMAGE.equalsIgnoreCase(type)) {
            QcCheckImageFragment qcCheckImageFragment = QcCheckImageFragment.newInstance();
            qcCheckImageFragment.setArguments(bundle);
            return qcCheckImageFragment;
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (rvpWithQCS != null && rvpWithQCS.rvpQualityCheckList != null && !rvpWithQCS.rvpQualityCheckList.isEmpty()) {
            return rvpWithQCS.rvpQualityCheckList.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    private SampleQuestion getCurrentSampleQuestion(String qcCode) {
        for (SampleQuestion sampleQuestion : sampleQuestions) {
            if (sampleQuestion.getCode().equalsIgnoreCase(qcCode)) {
                return sampleQuestion;
            }
        }
        return null;
    }
}

package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.info_window;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.LayoutMapTooltipBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;

@AndroidEntryPoint
public class MapInfoWindowDialog extends BaseDialog implements IMapInfoNavigator {
    private static MapInfoWindowDialog INSTANCE;
    @Inject
    MapInfoViewModel mapInfoViewModel;
    private LayoutMapTooltipBinding activityBinding;

    public static MapInfoWindowDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MapInfoWindowDialog();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activityBinding = DataBindingUtil.inflate(inflater, R.layout.layout_map_tooltip, container, false);
        View view = activityBinding.getRoot();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        activityBinding.executePendingBindings();
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }
}
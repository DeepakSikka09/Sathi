package in.ecomexpress.sathi.ui.drs.rvp.rvp_secure_activity;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentRvpSecureDeliveryBinding;
import in.ecomexpress.sathi.ui.base.BaseFragment;

@AndroidEntryPoint
public class RVPSecureDeliveryMapFragment extends BaseFragment<FragmentRvpSecureDeliveryBinding, RVPSecureDeliveryViewModel> {

    private final String TAG = RVPSecureDeliveryMapFragment.class.getSimpleName();
    @Inject
    RVPSecureDeliveryViewModel rvpSecureDeliveryViewModel;
    FragmentRvpSecureDeliveryBinding fragmentRvpSecureDeliveryBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentRvpSecureDeliveryBinding = getViewDataBinding();
        logScreenNameInGoogleAnalytics(TAG, getContext());
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        Objects.requireNonNull(supportMapFragment).getMapAsync(googleMap -> {
            if (!rvpSecureDeliveryViewModel.getDataManager().getStartQCLat().isEmpty() && !rvpSecureDeliveryViewModel.getDataManager().getStartQCLng().isEmpty()) {
                LatLng latLng = new LatLng(Double.parseDouble(rvpSecureDeliveryViewModel.getDataManager().getStartQCLat()), Double.parseDouble(rvpSecureDeliveryViewModel.getDataManager().getStartQCLng()));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_icon));
                googleMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public RVPSecureDeliveryViewModel getViewModel() {
        return rvpSecureDeliveryViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rvp_secure_delivery;
    }
}

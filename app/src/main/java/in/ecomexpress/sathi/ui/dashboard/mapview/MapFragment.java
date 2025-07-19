package in.ecomexpress.sathi.ui.dashboard.mapview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.backgroundServices.SathiLocationService;
import in.ecomexpress.sathi.databinding.FragmentMapBinding;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.utils.PreferenceUtils;
import in.ecomexpress.sathi.utils.cameraView.CameraSelfieActivity;

@AndroidEntryPoint
public class MapFragment extends BaseFragment<FragmentMapBinding, MapViewmodel> {
    private FragmentMapBinding mapBinding;
    @Inject
    MapViewmodel mdashBoardMapViewmodel;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private MapActivity mapActivity;
    double disanceformDestination;

    public MapFragment(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapBinding = getViewDataBinding();
        mdashBoardMapViewmodel.getDataManager().setScreenStatus(true);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(supportMapFragment).getMapAsync(googleMap -> {
            map = googleMap;
            addMarker(map);
        });

        mapBinding.btnNavigation.setOnClickListener(v -> {
            addMarker(map);
        });
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                mdashBoardMapViewmodel.getDataManager().getDCLatitude(),
                mdashBoardMapViewmodel.getDataManager().getDCLongitude());
        disanceformDestination = mdashBoardMapViewmodel.getDistanceBetweenLocations(destination);
        mapBinding.txtPrDetails.setText(mdashBoardMapViewmodel.getDistanceBetweenLocations(new com.google.maps.model.LatLng(mdashBoardMapViewmodel.getDataManager().getDCLatitude(), mdashBoardMapViewmodel.getDataManager().getDCLongitude())) + " " + " from SC location");

        if (disanceformDestination < mdashBoardMapViewmodel.getDataManager().getDCRANGE()) {
            mapBinding.kmCard.setVisibility(View.GONE);
            mapBinding.selfieAlertCard.setVisibility(View.VISIBLE);
            mapBinding.imageCard.setVisibility(View.VISIBLE);
            mapBinding.btnNavigation.setVisibility(View.GONE);
            supportMapFragment.getView().setVisibility(View.GONE);
            mapBinding.imgSelfieIcon.setVisibility(View.VISIBLE);
        }

        String dcName = mdashBoardMapViewmodel.getDataManager().getServiceCenter();
        if (dcName == null || dcName.isEmpty()) {
            mapBinding.txtScLocation.setText("NA");
        } else {
            mapBinding.txtScLocation.setText(dcName);
        }

        mapBinding.icCamera.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CameraSelfieActivity.class);
            startActivity(intent);
        });
        mapBinding.icLogOut.setOnClickListener(v -> {
            mdashBoardMapViewmodel.onLogoutClick();
            PreferenceUtils.clearPref(getBaseActivity());
        });
    }


    @Override
    public MapViewmodel getViewModel() {
        return mdashBoardMapViewmodel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_map;
    }


    private void addMarker(GoogleMap googleMap) {
        SathiLocationService.startLocationUpdates(requireActivity(), mdashBoardMapViewmodel.getDataManager());

        disanceformDestination = mdashBoardMapViewmodel.getDistanceBetweenLocations(new com.google.maps.model.LatLng(mdashBoardMapViewmodel.getDataManager().getDCLatitude(), mdashBoardMapViewmodel.getDataManager().getDCLongitude()));

        LatLng latLng = new LatLng(
                (mdashBoardMapViewmodel.getDataManager().getCurrentLatitude()),
                (mdashBoardMapViewmodel.getDataManager().getCurrentLongitude())
        );
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " and  " + latLng.longitude);

        LatLng latLng1 = new LatLng(
                (mdashBoardMapViewmodel.getDataManager().getDCLatitude()),
                (mdashBoardMapViewmodel.getDataManager().getDCLongitude()));
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLng1);
        markerOptions1.title("DC Location");

        googleMap.clear();
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_rider));
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dc));
        googleMap.addMarker(markerOptions);
        googleMap.addMarker(markerOptions1);

        if (disanceformDestination < mdashBoardMapViewmodel.getDataManager().getDCRANGE()) {
            mapBinding.kmCard.setVisibility(View.GONE);
            mapBinding.selfieAlertCard.setVisibility(View.VISIBLE);
            mapBinding.imageCard.setVisibility(View.VISIBLE);
            mapBinding.btnNavigation.setVisibility(View.GONE);
            supportMapFragment.getView().setVisibility(View.GONE);
            mapBinding.imgSelfieIcon.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(mapActivity, (disanceformDestination / 1000) + " km away from SC location", Toast.LENGTH_SHORT).show();
        }

        googleMap.addPolyline(new PolylineOptions()
                .add(latLng, latLng1)
                .width(5)
                .color(android.graphics.Color.RED));

        mapBinding.txtPrDetails.setText((disanceformDestination / 1000) + " " + " km from SC location");
    }
}
package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap;

import static android.content.Context.TELEPHONY_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.FragmentGoogleMapBinding;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.Location;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Details;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.drs.todolist.OnDRSListUpdateListener;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.MessageManager;

@AndroidEntryPoint
public class GBaseFragment extends BaseFragment<FragmentGoogleMapBinding, GViewModel> implements OnMapReadyCallback, IGFragmentNavigator, OnDRSListUpdateListener {

    private static final String TAG = GBaseFragment.class.getSimpleName();
    @Inject
    GViewModel gViewModel;
    private LatLng feLocation, dcLocation;
    private GoogleMap mMap;
    private ToDoListActivity activity;
    private List<CommonDRSListItem> listItemList;
    private List<LatLng> shipmentPoints = new ArrayList<>();
    FragmentGoogleMapBinding mBinding;
    private BottomSheetDialog mBottomSheetDialog;
    RecyclerView recyclerView;
    CustomAdapter mcustomAdapter = new CustomAdapter();

    public static GBaseFragment newInstance() {
        return new GBaseFragment();
    }

    @Override
    public void clearStack() {
        Toast.makeText(getActivity(), "You have logged in from another device. Please login again to use application.", Toast.LENGTH_LONG).show();
        Intent intent = LoginActivity.getStartIntent(getContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String message) {
        Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_SHORT);
    }

    @Override
    public GViewModel getViewModel() {
        return gViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_google_map;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateUI();
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(requireActivity().getApplicationContext());
        gViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = getViewDataBinding();
        try {
            initializeMap();
            if (this.listItemList != null && this.listItemList.isEmpty()) {
                hideProgress();
            } else {
                showProgress();
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void setContext(ToDoListActivity activity) {
        this.activity = activity;
    }

    private void showProgress() {
        mBinding.progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mBinding.progress.setVisibility(View.GONE);
    }

    @Override
    public void updateList(List<CommonDRSListItem> listItemList) {
        if (listItemList == null) {
            return;
        }
        this.listItemList = listItemList;
        if (isVisible()) {
            updateUI();
        }
    }

    private void updateUI() {
        if (!activity.isNetworkConnected()) {
            return;
        }
        showProgress();
        try {
            if (mMap != null) {
                mMap.clear();
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
        double lng = getViewModel().getCurrentLongitude();
        double lat = getViewModel().getCurrentLatitude();
        if (lat < 0 && lng < 0) {
            lat = SathiApplication.getGlobalFELat();
            lng = SathiApplication.getGlobalFELng();
        }
        try {
            feLocation = new LatLng(lat, lng);
            dcLocation = new LatLng(gViewModel.getDCLatitude(), gViewModel.getDCLongitude());
            Collections.sort(listItemList, CommonDRSListItem.seqNameComparator);
            for (Iterator<CommonDRSListItem> iter = listItemList.iterator(); iter.hasNext(); ) {
                CommonDRSListItem commonMapDrsListItem = iter.next();
                switch (commonMapDrsListItem.getType()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        try {
                            DRSForwardTypeResponse response = commonMapDrsListItem.getDrsForwardTypeResponse();
                            Location location = response.getConsigneeDetails().getAddress().getLocation();
                            if (location.getLat() == 0.0 && location.getLng() == 0.0) {
                                iter.remove();
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        try {
                            DRSReverseQCTypeResponse response = commonMapDrsListItem.getDrsReverseQCTypeResponse();
                            Location location = response.getConsigneeDetails().getAddress().getLocation();
                            if (location.getLat() == 0.0 && location.getLng() == 0.0) {
                                iter.remove();
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        try {
                            IRTSBaseInterface irtsBaseInterface = commonMapDrsListItem.getIRTSInterface();
                            in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.Location location = irtsBaseInterface.getDetails().getLocation();
                            lat = location.getLat();
                            lng = location.getLong();
                            if (lat == 0.0 && lng == 0.0)
                                iter.remove();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        try {
                            EDSResponse edsResponse = commonMapDrsListItem.getEdsResponse();
                            double latitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLat();
                            double longitude = edsResponse.getConsigneeDetail().getAddress().getLocation().getLng();
                            lat = latitude;
                            lng = longitude;
                            if (lat == 0.0 && lng == 0.0) {
                                iter.remove();
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        break;
                }
            }
            if (!listItemList.isEmpty()) {
                renderMap();
                gViewModel.createWayPoints(listItemList);
                hideProgress();
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onWayPointReady(List<LatLng> latLongs) {
        this.shipmentPoints = latLongs;
        if (this.shipmentPoints.isEmpty()) {
            setDestinationMarker(dcLocation);
            setSourceMarker(feLocation);
            return;
        }
        if (!(feLocation.latitude > 0 && feLocation.longitude > 0)) {
            getViewModel().writeErrors(System.currentTimeMillis(), "Not have felocation to get route : " + feLocation.toString());
            renderMap();
        } else {
            if (gViewModel != null) {
                setDestinationMarker(dcLocation);
                setSourceMarker(feLocation);
                focusOnFirstShipment();
                drawShipmentMarker();
            }
        }
    }

    private void focusOnFirstShipment() {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : shipmentPoints) {
                builder.include(latLng);
            }
            builder.include(dcLocation);
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.30);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.moveCamera(cu);
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void renderMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            MessageManager.showSampleToast(getContext(), title);
            try {
                showInfoWindow(marker);
            } catch (IndexOutOfBoundsException e) {
                Logger.e(TAG, String.valueOf(e));
            }
            return true;
        });
    }

    private void showInfoWindow(Marker marker) {
        mBottomSheetDialog = new BottomSheetDialog(activity);
        View view = getInfoWindow(activity, marker);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.show();
    }

    private void drawShipmentMarker() {
        for (int counter = 0; counter < listItemList.size(); counter++) {
            try {
                BitmapDescriptor bmpDescripter = null;
                String title = "";
                switch (listItemList.get(counter).getType()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD: {
                        DRSForwardTypeResponse response = listItemList.get(counter).getDrsForwardTypeResponse();
                        title = "" + response.getConsigneeDetails().getAddress().getLocation().getLat();
                        if (listItemList.get(counter).getDrsForwardTypeResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_dlvrd_marker);
                        } else if (listItemList.get(counter).getDrsForwardTypeResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_undlvrd_marker);
                        } else {
                            bmpDescripter = BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(requireActivity(), R.drawable.fwd, String.valueOf(response.getMap_sequence_no())));
                        }
                        break;
                    }
                    case GlobalConstant.ShipmentTypeConstants.EDS: {
                        EDSResponse response = listItemList.get(counter).getEdsResponse();
                        title = "" + response.getConsigneeDetail().getAddress().getLocation().getLat();
                        if (listItemList.get(counter).getEdsResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_dlvrd_marker);
                        } else if (listItemList.get(counter).getEdsResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_undlvrd_marker);
                        } else {
                            bmpDescripter = BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(requireActivity(), R.drawable.eds3, String.valueOf(response.getMap_sequence_no())));
                        }
                        break;
                    }
                    case GlobalConstant.ShipmentTypeConstants.RVP: {
                        DRSReverseQCTypeResponse response = listItemList.get(counter).getDrsReverseQCTypeResponse();
                        title = "" + response.getConsigneeDetails().getAddress().getLocation().getLat();
                        if (listItemList.get(counter).getDrsReverseQCTypeResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_dlvrd_marker);
                        } else if (listItemList.get(counter).getDrsReverseQCTypeResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_undlvrd_marker);
                        } else {
                            bmpDescripter = BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(requireActivity(), R.drawable.rvp1, String.valueOf(response.getMap_sequence_no())));
                        }
                        break;
                    }
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        Details details = listItemList.get(counter).getIRTSInterface().getDetails();
                        title = "" + details.getId();
                        if (listItemList.get(counter).getIRTSInterface().getDetails().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_dlvrd_marker);
                        } else if (listItemList.get(counter).getIRTSInterface().getDetails().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_undlvrd_marker);
                        } else {
                            bmpDescripter = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
                        }
                        break;
                }
                mMap.addMarker(new MarkerOptions().position(shipmentPoints.get(counter)).title(title).icon(bmpDescripter));
            } catch (Exception e) {
                getBaseActivity().showSnackbar(e.getMessage());
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }


    public Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setTextSize((int) (18 * scale));
        paint.setShadowLayer(1f, 1f, 1f, Color.WHITE);
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 3;
        int y = (bitmap.getHeight() + bounds.height()) / 3;
        canvas.drawText(gText, x, y, paint);
        return bitmap;
    }


    @Override
    public void onResume() {
        super.onResume();
        notifyDataSetChanged();
        mcustomAdapter.resetClickVariables();
    }

    public void notifyDataSetChanged() {
        mcustomAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setSourceMarker(LatLng location) {
        try {
            mMap.addMarker(new MarkerOptions().position(location).title("-1").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start_strip)));
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void setDestinationMarker(LatLng location) {
        try {
            mMap.addMarker(new MarkerOptions().position(location).title("-1").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_stop_strip)));
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public View getInfoWindow(Context mContext, Marker marker) {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_map_tooltip, null);
        recyclerView = view.findViewById(R.id.recyclerView);
        Button close = view.findViewById(R.id.button);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        HashMap<Double, HashSet<CommonDRSListItem>> loc = new HashMap<>();
        Double location = null;
        HashSet<CommonDRSListItem> commonMapDrsListItem_map;
        for (int i = 0; i < listItemList.size(); i++) {
            if (listItemList.get(i).getType().equalsIgnoreCase(Constants.FWD)) {
                location = listItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();
            } else if (listItemList.get(i).getType().equalsIgnoreCase(Constants.RVP)) {
                location = listItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();

            } else if (listItemList.get(i).getType().equalsIgnoreCase(Constants.EDS)) {
                location = listItemList.get(i).getEdsResponse().getConsigneeDetail().getAddress().getLocation().getLat();
            }
            if (String.valueOf(location).equalsIgnoreCase(marker.getTitle())) {
                if (!loc.containsKey(location)) {
                    commonMapDrsListItem_map = new HashSet<>();
                    commonMapDrsListItem_map.add(listItemList.get(i));
                    loc.put(location, commonMapDrsListItem_map);
                } else {
                    Iterator<Double> myVeryOwnIterator = loc.keySet().iterator();
                    double key = myVeryOwnIterator.next();
                    HashSet<CommonDRSListItem> value = loc.get(key);
                    assert value != null;
                    value.add(listItemList.get(i));
                    loc.put(location, value);
                }
            }
        }
        HashSet<CommonDRSListItem> commonMapDrsListItem_set_value = null;
        for (double key : loc.keySet()) {
            if (key == Double.parseDouble(marker.getTitle())) {
                commonMapDrsListItem_set_value = loc.get(key);
                break;
            }
        }
        List<CommonDRSListItem> list = new ArrayList<>(commonMapDrsListItem_set_value);
        mcustomAdapter.setData(mContext, list, activity, gViewModel);
        recyclerView.setAdapter(mcustomAdapter);
        close.setOnClickListener(view1 -> mBottomSheetDialog.dismiss());
        return view;
    }

    public boolean startCallIntent(String pstn, Context activity) {
        try {
            TelephonyManager tMgr = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            int simState = tMgr.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                Toast.makeText(activity, "No Sim Found!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + Uri.encode(pstn)));
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            activity.startActivity(intent);
        } catch (Exception e) {
            getBaseActivity().showSnackbar(e.getMessage());
            Logger.e(TAG, String.valueOf(e));
            return false;
        }
        return true;
    }

    public boolean isBottomSheetVisible = false;
    public BottomSheetDialog bottomSheetDialog;
}
package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public interface IGFragmentNavigator {

    void showError(String message);

    void onWayPointReady(List<LatLng> latLngs);

    void clearStack();
}
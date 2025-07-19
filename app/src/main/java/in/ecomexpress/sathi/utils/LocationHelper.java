package in.ecomexpress.sathi.utils;

import android.location.Location;

public class LocationHelper {

    public static int getDistanceBetweenPoint(double sourceLat, double sourceLang, double destLat, double destLang) {
        Location sourceLocation = new Location("");
        sourceLocation.setLatitude(sourceLat);
        sourceLocation.setLongitude(sourceLang);
        Location destLocation = new Location("");
        destLocation.setLatitude(destLat);
        destLocation.setLongitude(destLang);
        return (int) sourceLocation.distanceTo(destLocation);
    }
}
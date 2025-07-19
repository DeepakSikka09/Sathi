package in.ecomexpress.sathi.utils;

public class ShipmentUtils {
    public static String getShipmentStatus(int status) {
        switch (status) {
            case 0:
                return GlobalConstant.ShipmentStatusConstants.ASSIGNED;
            case 1:
                return GlobalConstant.ShipmentStatusConstants.DELIVERED;
            case 2:
                return GlobalConstant.ShipmentStatusConstants.SYNC;
            case 3:
                return GlobalConstant.ShipmentStatusConstants.UNDELIVERED;

        }
        return "";

    }
}

package in.ecomexpress.sathi.utils;

import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
public class DRSListSorter {

    private final static String TAG = DRSListSorter.class.getName();
    private static final int MAX_INT_LIMIT = 3;
    static String rtsData = "";

    public enum TECHNIQUE {
        DISTANCE, DRS_NO
    }

    private static TECHNIQUE technique;

    public static List<CommonDRSListItem> sort(List<CommonDRSListItem> listDRS, TECHNIQUE tech) {
        List<CommonDRSListItem> list = new ArrayList<>();
        technique = tech;
        Logger.e("", "Before Sorting");
        printDRS(listDRS);
        list = sortObject(listDRS);
        Logger.e(TAG, "After Sorting");
        printDRS(list);
        return list;
    }

    private static void printDRS(List<CommonDRSListItem> list) {
        for (CommonDRSListItem commonDRSListItem : list) {
            String resultset = getCompareValue(commonDRSListItem);
            resultset = String.valueOf(getCompareValue(commonDRSListItem));
            Logger.e("SORTING: ", resultset);
        }
    }

    static void merge(CommonDRSListItem[] arr, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        CommonDRSListItem[] L = new CommonDRSListItem[n1];
        CommonDRSListItem[] R = new CommonDRSListItem[n2];

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];


        /* Merge the temp arrays */

        // Initial indexes of first and second subarrays
        int i = 0, j = 0;

        // Initial index of merged subarry array
        int k = l;
        while (i < n1 && j < n2) {
            switch (technique) {
                case DRS_NO: {
//                    long drsNoL = getCompareValue(L[i]);
//                    long drsNoR = getCompareValue(R[j]);
//                    if (drsNoL - drsNoR < 0) {
                    String drsNoL = getCompareValue(L[i]);
                    String drsNoR = getCompareValue(R[j]);
                    if (drsNoL.compareToIgnoreCase(drsNoR) > 0) {
                        arr[k] = L[i];
                        i++;
                    } else {
                        arr[k] = R[j];
                        j++;
                    }
                }
                break;
                case DISTANCE: {
                    double leftD = getDistance(L[i]);
                    double rightD = getDistance(R[j]);
                    if (leftD < rightD) {
                        arr[k] = L[i];
                        i++;
                    } else {
                        arr[k] = R[j];
                        j++;
                    }
                }
                break;
            }

            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }

    }
//
//    public String getAWBNo(CommonDRSListItem commonDRSListItem) {
//        if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
//            return String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
//        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
//            return String.valueOf(commonDRSListItem.getIRTSInterface().getDetails().getId());
//        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
//            return String.valueOf(commonDRSListItem.getDrsReturnToShipperTypeResponse().getAwbNo());
//        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
//            return String.valueOf(commonDRSListItem.getEdsResponse().getAwbNo());
//        }
//        return commonDRSListItem.getType();
//    }

    private static double getDistance(CommonDRSListItem commonDRSListItem) {
        if (getShipmentStatus(commonDRSListItem) > 0) {
            return 1000000000;
        }
        return commonDRSListItem.getDistance();
    }


    private static List<CommonDRSListItem> sortObject(List<CommonDRSListItem> commonDRSListItemList) {
        CommonDRSListItem[] commonDRSListItems = commonDRSListItemList.toArray(new CommonDRSListItem[commonDRSListItemList.size()]);
        sort(commonDRSListItems, 0, commonDRSListItems.length - 1);
        List<CommonDRSListItem> list = new ArrayList<>();
        Logger.e(TAG, "DRSSorter " + technique.toString());
        for (CommonDRSListItem commonDRSListItem : commonDRSListItems) {
            Logger.e(TAG, "commonDRSListItem" + "[D: " + commonDRSListItem.getDistance() + "]" + "[DRSNo: " + getDRSNo(commonDRSListItem) + "]");
            list.add(commonDRSListItem);
        }

        return list;
    }

    // Main function that sorts arr[l..r] using
    // merge()
    private static void sort(CommonDRSListItem[] arr, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Sort first and second halves
            sort(arr, l, m);
            sort(arr, m + 1, r);

            // Merge the sorted halves
            merge(arr, l, m, r);
        }
    }

    private static String getCompareValue(CommonDRSListItem commonDRSListItem) {
        String resultStr = "";
        try {
            if (isSynced(commonDRSListItem)) {
                resultStr = "A" + getDRSNo(commonDRSListItem) + "" + getFinalModifiedString(String.valueOf(getSequanceNo(commonDRSListItem)));
            }else if (isUndelievered(commonDRSListItem)) {
                resultStr = "C" + getDRSNo(commonDRSListItem) + "" + getFinalModifiedString(String.valueOf(getSequanceNo(commonDRSListItem)));
            } else if (isDelievered(commonDRSListItem)) {
                resultStr = "B" + getDRSNo(commonDRSListItem) + "" + getFinalModifiedString(String.valueOf(getSequanceNo(commonDRSListItem)));
            }  else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
//                if (rtsData.isEmpty())
                resultStr = "D";
//                else {
//                    resultStr = "B" + rtsData;
//                }
            } else {
                rtsData = getDRSNo(commonDRSListItem) + "" + getFinalModifiedString(String.valueOf(getSequanceNo(commonDRSListItem)));
                resultStr = "" +
                        "E" + getDRSNo(commonDRSListItem) + "" + getFinalModifiedString(String.valueOf(getSequanceNo(commonDRSListItem)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "E";
        }
//        resultStr = getFinalModifiedString(resultStr);
//        return Long.parseLong(resultStr);
        return resultStr;
    }

    private static String getFinalModifiedString(String str) {
        try {
            int length = str.length();
            if (length == 0) {
                str = "000";
            } else if (length == 1) {
                str = "00" + str;
            } else if (length == 2) {
                str = "0" + str;
            } else if (length == 3) {
                str = str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String addZeros(String str, int no) {
        StringBuilder sbr = new StringBuilder(str);
        if (no == 1) {
            sbr.append("0").append(str);
        } else if (no == 2) {
            sbr.append("00").append(str);
        } else {
            return str;
        }
//        for (int i = 1; i <= no; i++) {
//            sbr.append("0");
//        }
        return sbr.toString();
    }

    private static boolean isSynced(CommonDRSListItem commonDRSListItem) {
        if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
            return commonDRSListItem.getDrsForwardTypeResponse().getShipmentSyncStatus() == Constants.SYNCED;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
            return commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentSyncStatus() == Constants.SYNCED;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
            return commonDRSListItem.getEdsResponse().getShipmentSyncStatus() == Constants.SYNCED;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
            return commonDRSListItem.getIRTSInterface().getDetails().getShipmentSyncStatus() == Constants.SYNCED;
        }
        return false;
    }

    private static boolean isDelievered(CommonDRSListItem commonDRSListItem) {
        if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
            return commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
            return commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
            return commonDRSListItem.getEdsResponse().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
            return commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() == Constants.SHIPMENT_DELIVERED_STATUS;
        }
        return false;
    }

    private static boolean isUndelievered(CommonDRSListItem commonDRSListItem) {
        if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
            return commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
            return commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
            return commonDRSListItem.getEdsResponse().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS;
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
            return commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS;
        }
        return false;
    }

    private static int getShipmentStatus(CommonDRSListItem commonDRSListItem) {
        try {
            if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
//                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() == 1) {
//                    return -2;
//                } else if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() == 2) {
//                    return -3;
//                } else {
                return commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus();
//                }

            } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
//                if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == 1) {
//                    return -2;
//                } else if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == 2) {
//                    return -3;
//                } else if(commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentSyncStatus()==2){
//                    return -4;
//                }else {
                return commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus();
//                }
            } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
                return commonDRSListItem.getEdsResponse().getShipmentStatus();
            } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RTS) {
                return commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    private static int getSequanceNo(CommonDRSListItem commonDRSListItem) {
        try {
            if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
                return 999-commonDRSListItem.getDrsForwardTypeResponse().getSequenceNo();
            } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
                return 999-commonDRSListItem.getDrsReverseQCTypeResponse().getSequenceNo();
            } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
                return 999-commonDRSListItem.getEdsResponse().getSequenceNo();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private static int getDRSNo(CommonDRSListItem commonDRSListItem) {
        if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.FWD) {
            return commonDRSListItem.getDrsForwardTypeResponse().getDrsId();
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.RVP) {
            return commonDRSListItem.getDrsReverseQCTypeResponse().getDrs();
        } else if (commonDRSListItem.getType() == GlobalConstant.ShipmentTypeConstants.EDS) {
            return commonDRSListItem.getEdsResponse().getDrsNo();
        }

        return 0;
    }

}

package in.ecomexpress.sathi.repo;

import java.util.List;
import java.util.Set;

import in.ecomexpress.sathi.repo.local.db.IDBHelper;
import in.ecomexpress.sathi.repo.local.pref.IPreferenceHelper;
import in.ecomexpress.sathi.repo.remote.IRestApiHelper;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import io.reactivex.Observable;

public interface IDataManager extends IDBHelper, IRestApiHelper, IPreferenceHelper {

    void setUserAsLoggedOut();

    void updateUserInfo(
            LoggedInMode loggedInMode,
            String authToken,
            String serviceCenter,
            String name,
            String designation,
            String mobile,
            long locationType,
            String locationCode,
            String code,
            boolean isUserValidateted,
            String photoUrl, String ITExectiveNo);

    void startTripInfo(Long actualMeterReading, String tripId, String vehicleType, String vehicleOwnerType, String routeName);

    void setVodaOrderNo(String orderNo);

    Observable<Set<RVPReasonCodeMaster>> getSubgroupFromRvpReasonCode(String isSecure);





    enum LoggedInMode {
        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_SERVER(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_GOOGLE(3);
        private final int mType;
        LoggedInMode(int type) {
            mType = type;
        }
        public int getType() {
            return mType;
        }
    }
}

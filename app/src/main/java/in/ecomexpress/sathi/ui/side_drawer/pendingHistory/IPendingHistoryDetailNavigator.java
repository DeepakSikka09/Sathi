package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import java.util.List;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;

public interface IPendingHistoryDetailNavigator {

    void setSuccessName();

    void showError(String message);

    void setDetails(PushApi pushApis_data);

    void setDRSStatus(int shipmentSyncStatus);

    void setImagesData(List<ImageModel> imageModels);
}
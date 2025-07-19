package in.ecomexpress.sathi.ui.side_drawer.pendingHistory;

import java.util.List;
import in.ecomexpress.sathi.repo.local.data.commit.PushApi;

public interface IPendingHistoryNavigator {

    void setPacketData(List<PushApi> pushApis);

    void showError(String message);
}
package in.ecomexpress.sathi.ui.drs.forward.obd.navigator;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;

public interface IOBDProductDetailNavigator {

    void onUndelivered(ForwardCommit forwardCommit);

    void onHandleError(String errorResponse);
}
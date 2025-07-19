package in.ecomexpress.sathi.ui.drs.forward.mps;

import in.ecomexpress.sathi.repo.local.data.fwd.ForwardCommit;



public interface MPSScanNavigator {

    void unDelivered(ForwardCommit forwardCommit);



    void onProceed();

    void onErrorMsg(String e);
}

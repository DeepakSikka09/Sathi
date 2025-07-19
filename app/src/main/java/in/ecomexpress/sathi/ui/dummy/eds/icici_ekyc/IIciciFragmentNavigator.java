package in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc;

import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;

public interface IIciciFragmentNavigator {
    void ongetPid();
    void validateUrn();

    void sendPidDetail(String name);
    void sendICICIResponse(IciciResponse iciciResponse);

    void sendData(String name);
}

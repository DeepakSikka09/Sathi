package in.ecomexpress.sathi.ui.dummy.eds.icic_standard;

import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse_standard;

public interface IIciciFragmentStandardNavigator {
    void ongetPid();
    void validateUrn();

    void sendPidDetail(String name);
    void sendICICIResponse(IciciResponse_standard iciciResponse);

    void sendData(String name);
}

package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.common.CInterface;

public interface IRTSBaseInterface extends CInterface {
    Details getDetails();

    void setDetails(Details details);

    List<ShipmentsDetail> getShipmentsDetails();

    void setShipmentsDetails(List<ShipmentsDetail> shipmentsDetails);

    String filterValue();
}

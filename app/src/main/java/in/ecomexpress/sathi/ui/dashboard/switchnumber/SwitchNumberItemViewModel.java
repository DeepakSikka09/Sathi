package in.ecomexpress.sathi.ui.dashboard.switchnumber;

import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;
import in.ecomexpress.sathi.utils.Constants;

public class SwitchNumberItemViewModel {
    public CbPstnOptions cbPstnOptions;
    public ItemListener itemListener;
    String getPstnformat;
    String setPstnFormat;

    public SwitchNumberItemViewModel(CbPstnOptions cbPstnOptions, ItemListener itemListener) {
        this.cbPstnOptions = cbPstnOptions;
        this.itemListener = itemListener;
    }

    public String PstnFormat() {
        try {
            getPstnformat = cbPstnOptions.getPstn_format();
            if (getPstnformat != null) {
                if (getPstnformat.contains(Constants.pstn_pin)) {
                    setPstnFormat = getPstnformat.replaceAll(",@@PIN@@#", "");
                } else if (getPstnformat.contains(Constants.pstn_awb)) {
                    setPstnFormat = getPstnformat.replaceAll(",@@AWB@@#", "");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return setPstnFormat;
    }

    public String PstnProvider() {
        return cbPstnOptions.getPstn_provider();
    }

    public String IsLocal() {
        return cbPstnOptions.getIsLocal();
    }

    public String Additional() {
        return cbPstnOptions.getAdditional_text();
    }

    public void onItemClick() {
        itemListener.onItemClick(cbPstnOptions);
    }

}

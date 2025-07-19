package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

/**
 * Created by santosh on 20/8/19.
 */

public class DeviceInfo {
    private Additional_info additional_info;

    private String mc;

    private String dpId;

    private String rdsId;

    private String mi;

    private String rdsVer;

    private String dc;

    public Additional_info getAdditional_info ()
    {
        return additional_info;
    }

    public void setAdditional_info (Additional_info additional_info)
    {
        this.additional_info = additional_info;
    }

    public String getMc ()
    {
        return mc;
    }

    public void setMc (String mc)
    {
        this.mc = mc;
    }

    public String getDpId ()
    {
        return dpId;
    }

    public void setDpId (String dpId)
    {
        this.dpId = dpId;
    }

    public String getRdsId ()
    {
        return rdsId;
    }

    public void setRdsId (String rdsId)
    {
        this.rdsId = rdsId;
    }

    public String getMi ()
    {
        return mi;
    }

    public void setMi (String mi)
    {
        this.mi = mi;
    }

    public String getRdsVer ()
    {
        return rdsVer;
    }

    public void setRdsVer (String rdsVer)
    {
        this.rdsVer = rdsVer;
    }

    public String getDc ()
    {
        return dc;
    }

    public void setDc (String dc)
    {
        this.dc = dc;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [additional_info = "+additional_info+", mc = "+mc+", dpId = "+dpId+", rdsId = "+rdsId+", mi = "+mi+", rdsVer = "+rdsVer+", dc = "+dc+"]";
    }
}

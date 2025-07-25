package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

/**
 * Created by santosh on 20/8/19.
 */

public class PidData {
    private String Hmac;

    private Resp Resp;

    private DeviceInfo DeviceInfo;

    private Data Data;

    private Skey Skey;

    public String getHmac ()
    {
        return Hmac;
    }

    public void setHmac (String Hmac)
    {
        this.Hmac = Hmac;
    }

    public Resp getResp ()
    {
        return Resp;
    }

    public void setResp (Resp Resp)
    {
        this.Resp = Resp;
    }

    public DeviceInfo getDeviceInfo ()
    {
        return DeviceInfo;
    }

    public void setDeviceInfo (DeviceInfo DeviceInfo)
    {
        this.DeviceInfo = DeviceInfo;
    }

    public Data getData ()
    {
        return Data;
    }

    public void setData (Data Data)
    {
        this.Data = Data;
    }

    public Skey getSkey ()
    {
        return Skey;
    }

    public void setSkey (Skey Skey)
    {
        this.Skey = Skey;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Hmac = "+Hmac+", Resp = "+Resp+", DeviceInfo = "+DeviceInfo+", Data = "+Data+", Skey = "+Skey+"]";
    }
}

package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

/**
 * Created by santosh on 20/8/19.
 */

public class Additional_info {
    private Param[] Param;

    public Param[] getParam ()
    {
        return Param;
    }

    public void setParam (Param[] Param)
    {
        this.Param = Param;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Param = "+Param+"]";
    }
}

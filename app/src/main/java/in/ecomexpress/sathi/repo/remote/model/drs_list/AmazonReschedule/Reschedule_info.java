package in.ecomexpress.sathi.repo.remote.model.drs_list.AmazonReschedule;

import java.util.ArrayList;
import java.util.List;

public class Reschedule_info
{
    private String commitmentEndPeriod;

    private ValidDays[] validDays;

    public String getCommitmentEndPeriod ()
    {
        return commitmentEndPeriod;
    }

    public void setCommitmentEndPeriod (String commitmentEndPeriod)
    {
        this.commitmentEndPeriod = commitmentEndPeriod;
    }

    public ValidDays[] getValidDays ()
    {
        return validDays;
    }

    public void setValidDays (ValidDays[] validDays)
    {
        this.validDays = validDays;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [commitmentEndPeriod = "+commitmentEndPeriod+", validDays = "+validDays+"]";
    }
}
package in.ecomexpress.sathi.repo.remote.model.dp_daily_earned;

public class DPReferenceCodeResponse
{
    private String description;

    private boolean status;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public boolean getStatus ()
    {
        return status;
    }

    public void setStatus (boolean status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [description = "+description+", status = "+status+"]";
    }
}
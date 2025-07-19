package in.ecomexpress.sathi.repo.remote.model.eds;

public class AadharStatusResponse
{
    private String status_code;

    private String description;

    private Masking_status[] masking_status;

    private String status;

    public String getStatus_code ()
    {
        return status_code;
    }

    public void setStatus_code (String status_code)
    {
        this.status_code = status_code;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Masking_status[] getMasking_status ()
    {
        return masking_status;
    }

    public void setMasking_status (Masking_status[] masking_status)
    {
        this.masking_status = masking_status;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [status_code = "+status_code+", description = "+description+", masking_status = "+masking_status+", status = "+status+"]";
    }
}
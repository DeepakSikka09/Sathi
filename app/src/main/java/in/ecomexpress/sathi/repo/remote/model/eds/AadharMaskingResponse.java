package in.ecomexpress.sathi.repo.remote.model.eds;

public class AadharMaskingResponse
{
    private Response_data[] response_data;

    private String description;

    private String status;

    public Response_data[] getResponse_data ()
    {
        return response_data;
    }

    public void setResponse_data (Response_data[] response_data)
    {
        this.response_data = response_data;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
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
        return "ClassPojo [response_data = "+response_data+", description = "+description+", status = "+status+"]";
    }
}
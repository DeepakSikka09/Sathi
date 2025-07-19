package in.ecomexpress.sathi.repo.remote.model.hospital;



public class Hospital
{
    private String description;

    private Covid_hospitals[] covid_hospitals;

    private boolean status;

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public Covid_hospitals[] getCovid_hospitals ()
    {
        return covid_hospitals;
    }

    public void setCovid_hospitals (Covid_hospitals[] covid_hospitals)
    {
        this.covid_hospitals = covid_hospitals;
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
        return "ClassPojo [description = "+description+", covid_hospitals = "+covid_hospitals+", status = "+status+"]";
    }
}
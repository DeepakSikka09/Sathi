package in.ecomexpress.sathi.repo.remote.model.hospital;

public class Covid_hospitals
{
    private String phoneNumber;

    private String altPhoneNumber;

    private String hospitalAddress;

    private String id;

    private String hospitalName;

    private String cityId;

    private String addedOn;

    public String getPhoneNumber ()
    {
        return phoneNumber;
    }

    public void setPhoneNumber (String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getAltPhoneNumber ()
    {
        return altPhoneNumber;
    }

    public void setAltPhoneNumber (String altPhoneNumber)
    {
        this.altPhoneNumber = altPhoneNumber;
    }

    public String getHospitalAddress ()
    {
        return hospitalAddress;
    }

    public void setHospitalAddress (String hospitalAddress)
    {
        this.hospitalAddress = hospitalAddress;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getHospitalName ()
    {
        return hospitalName;
    }

    public void setHospitalName (String hospitalName)
    {
        this.hospitalName = hospitalName;
    }

    public String getCityId ()
    {
        return cityId;
    }

    public void setCityId (String cityId)
    {
        this.cityId = cityId;
    }

    public String getAddedOn ()
    {
        return addedOn;
    }

    public void setAddedOn (String addedOn)
    {
        this.addedOn = addedOn;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [phoneNumber = "+phoneNumber+", altPhoneNumber = "+altPhoneNumber+", hospitalAddress = "+hospitalAddress+", id = "+id+", hospitalName = "+hospitalName+", cityId = "+cityId+", addedOn = "+addedOn+"]";
    }
}
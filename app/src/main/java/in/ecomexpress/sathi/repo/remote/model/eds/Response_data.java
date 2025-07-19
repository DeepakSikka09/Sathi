package in.ecomexpress.sathi.repo.remote.model.eds;

public class Response_data
{
    private String image_code;

    private String image_id;

    public String getImage_code ()
    {
        return image_code;
    }

    public void setImage_code (String image_code)
    {
        this.image_code = image_code;
    }

    public String getImage_id ()
    {
        return image_id;
    }

    public void setImage_id (String image_id)
    {
        this.image_id = image_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [image_code = "+image_code+", image_id = "+image_id+"]";
    }
}
package in.ecomexpress.sathi.repo.remote.model;

public class EdsImageStatus {

    private int image_position;
    private String image_status;   // M or O

    public EdsImageStatus()
    {

    }

    public int getImage_position() {
        return image_position;
    }

    public void setImage_position(int image_position) {
        this.image_position = image_position;
    }

    public String getImage_status() {
        return image_status;
    }

    public void setImage_status(String image_status) {
        this.image_status = image_status;
    }
}

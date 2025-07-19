package in.ecomexpress.sathi.repo.remote.model.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageUploadResponse {

    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("status")
    private String status;
    @JsonProperty("description")
    private String description;

    @JsonProperty("card_status")
    private String card_status;

    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("file_size")
    private Integer fileSize;
    @JsonProperty("image_id")
    private Integer imageId;

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }


    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("file_name")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("file_type")
    public String getFileType() {
        return fileType;
    }

    @JsonProperty("file_type")
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @JsonProperty("file_size")
    public Integer getFileSize() {
        return fileSize;
    }

    @JsonProperty("file_size")
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "ImageUploadResponse{" +
                "fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }

    public String getCard_status(){
        return card_status;
    }

    public void setCard_status(String card_status){
        this.card_status = card_status;
    }
}


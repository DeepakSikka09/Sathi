package in.ecomexpress.sathi.repo.remote.model.trip;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SelfieImageResponse {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("description")
    private String description;


    @JsonProperty("file_type")
    private String fileType;


    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("file_size")
    private Long file_size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }
}

package in.ecomexpress.sathi.repo.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorResponse implements Parcelable {

    @Ignore
    @JsonProperty("status")
    public boolean status;

    @Ignore
    @JsonProperty("response")
    public EResponse eResponse;

    public static final Creator<ErrorResponse> CREATOR = new Creator<ErrorResponse>() {
        @Override
        public ErrorResponse createFromParcel(Parcel in) {
            return new ErrorResponse(in);
        }

        @Override
        public ErrorResponse[] newArray(int size) {
            return new ErrorResponse[size];
        }
    };

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("response")
    public EResponse getEResponse() {
        return eResponse;
    }

    @JsonProperty("response")
    public void setEResponse(EResponse response) {
        this.eResponse = response;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class EResponse implements Parcelable {

        @JsonProperty("status-code")
        @Ignore
        private long statusCode;

        @Ignore
        @JsonProperty("description")
        private String description;

        @JsonProperty("status-code")
        public long getStatusCode() {
            return statusCode;
        }

        @JsonProperty("status-code")
        public void setStatusCode(long statusCode) {
            this.statusCode = statusCode;
        }

        @JsonProperty("description")
        public String getDescription() {
            return description;
        }

        @JsonProperty("description")
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.statusCode);
            dest.writeString(this.description);
        }

        public EResponse() {
        }

        protected EResponse(Parcel in) {
            this.statusCode = in.readLong();
            this.description = in.readString();
        }

        public static final Creator<EResponse> CREATOR = new Creator<EResponse>() {
            @Override
            public EResponse createFromParcel(Parcel source) {
                return new EResponse(source);
            }

            @Override
            public EResponse[] newArray(int size) {
                return new EResponse[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.eResponse, flags);
    }

    public ErrorResponse() {
    }

    protected ErrorResponse(Parcel in) {
        this.status = in.readByte() != 0;
        this.eResponse = in.readParcelable(EResponse.class.getClassLoader());
    }

}
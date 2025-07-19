package in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo;

/**
 * Created by umangagarwal on 26,August,2019
 */
public class PayloadEncPublicKey {
    public String id;
    private String key;
    public long expiry;

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public long getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return "PayloadEncPublicKey{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", expiry=" + expiry +
                '}';
    }
}

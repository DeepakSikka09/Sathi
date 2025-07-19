package in.ecomexpress.sathi.repo.local.db.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"image_path"}, unique = true)})
public class RVPQCImageTable {

    @PrimaryKey(autoGenerate = true)
    public Long id;
    public Long awb_number;
    public String image_path = "";
}

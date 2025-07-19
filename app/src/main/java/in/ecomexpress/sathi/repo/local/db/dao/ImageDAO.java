package in.ecomexpress.sathi.repo.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import in.ecomexpress.sathi.repo.local.db.model.ImageModel;

@Dao
public interface ImageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ImageModel imageModel);

    @Query("UPDATE Image SET status= :status WHERE imageName = :imageName")
    void updateImageStatus(String imageName, int status);

    @Query("UPDATE Image SET imageId= :imageId WHERE imageName = :imageName")
    void updateImageID(String imageName, int imageId);

    @Query("Select * from Image where awbNo = :awbNo")
    List<ImageModel> getImages(String awbNo);

    @Query("Select * from Image where imageType = :imagetype")
    List<ImageModel> getUDImage(String imagetype);

    @Query("Select * from Image where  status = '0' OR status ='4'")
    List<ImageModel> getUnSyncImageV2();

    @Query("Select * from Image where awbNo = :awb")
    List<ImageModel> getImageStatus(String awb);

    @Query("Select * from Image where awbNo = :awb")
    List<ImageModel> getShipmentImageStatus(String awb);

    @Query("delete from Image where awbNo = :awb")
    void deleteImage(String awb);

    @Query("DELETE FROM Image")
    void nukeTable();
}

package in.ecomexpress.sathi.repo.local.db.db_utils;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.quality_checks;

public class QualityChecksConverter {

    @TypeConverter
    public static List<quality_checks> fromString(String value) {
        Type listType = new TypeToken<List<quality_checks>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<quality_checks> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
package in.ecomexpress.sathi.repo.local.db.db_utils;

import androidx.room.TypeConverter;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.qc_item;

public class QcItemConverter {

    @TypeConverter
    public static ArrayList<qc_item> fromString(String value) {
        Type listType = new TypeToken<ArrayList<qc_item>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<qc_item> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}

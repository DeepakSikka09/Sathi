package in.ecomexpress.sathi.repo.local.db.db_utils;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.mps.RvpMpsQualityCheck;

public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static String fromQualityCheck(List<RvpMpsQualityCheck> qualityCheck) {
        Gson gson = new Gson();
        String json = gson.toJson(qualityCheck);
        return json;
    }

    @TypeConverter
    public static List<RvpMpsQualityCheck> fromStringToQualityCheck(String value) {
        Type listType = new TypeToken<List<RvpMpsQualityCheck>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }
}
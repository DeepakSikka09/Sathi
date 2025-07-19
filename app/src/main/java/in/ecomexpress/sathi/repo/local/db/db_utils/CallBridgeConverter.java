package in.ecomexpress.sathi.repo.local.db.db_utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.callbridge_details;


public class CallBridgeConverter {
    @TypeConverter
    public static ArrayList<callbridge_details> fromString(String value) {
        Type listType = new TypeToken<ArrayList<callbridge_details>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<callbridge_details> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}

package com.oustme.oustsdk.room;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TCUserCardData {

    @TypeConverter
    public static List<EntityUserCardData> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<EntityUserCardData>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<EntityUserCardData> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }
}

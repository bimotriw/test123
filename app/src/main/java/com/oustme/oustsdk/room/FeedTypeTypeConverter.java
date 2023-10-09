package com.oustme.oustsdk.room;


import androidx.room.TypeConverter;

import com.oustme.oustsdk.firebase.common.FeedType;

class FeedTypeTypeConverter {
    @TypeConverter
    public static FeedType stringToSomeObjectList(String data) {
        return FeedType.valueOf(data);
    }

    @TypeConverter
    public static String someObjectListToString(FeedType someObjects) {
        return someObjects.toString();
    }
}

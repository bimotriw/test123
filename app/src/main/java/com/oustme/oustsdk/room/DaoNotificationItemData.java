package com.oustme.oustsdk.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.oustme.oustsdk.room.dto.EntityNotificationData;

import java.util.List;

@Dao
public interface DaoNotificationItemData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotificationItemData(EntityNotificationData entityNotificationData);

    // Get All Notifications.
    @Query("select * from EntityNotificationData where userId = :studentKey order by updateTime desc")
    List<EntityNotificationData> getNotificationData(String studentKey);

    // Check RoomBD Based on Key value Exist or not.
    @Query("select * from EntityNotificationData where keyValue = :keyValue limit 1")
    EntityNotificationData getNotificationById(String keyValue);

    // Check RoomBD Based on ContentId value Exist or not.
    @Query("select * from EntityNotificationData where ContentId = :ContentId limit 1")
    EntityNotificationData getNotificationByContentId(String ContentId);

    // UpDate Read Notification Status Based on contentId.
    @Query("UPDATE EntityNotificationData SET isRead = :readData where contentId = :contentId")
    int upDateNotificationReadStatus(String readData, String contentId);

    @Query("delete from EntityNotificationData")
    void deleteEntityNotificationData();

    // UpDate Read Notification Status Based on keyValue.
    @Query("UPDATE EntityNotificationData SET isRead = :isRead where keyValue = :keyId")
    int upDateReadFireBaseNotificationStatus(String isRead, String keyId);

    // Deleting offline Notifications
    @Query("delete from EntityNotificationData where ContentId = :ContentId")
    int deleteOfflineNotifications(String ContentId);
}

package com.oustme.oustsdk.notification.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.notification.NotificationActivity;
import com.oustme.oustsdk.notification.model.NotificationComponentModel;
import com.oustme.oustsdk.notification.model.NotificationResponse;
import com.oustme.oustsdk.notification.repository.NotificationRepository;

import java.util.ArrayList;

public class NotificationViewModel extends ViewModel {
    private NotificationRepository notificationRepository;
    private MutableLiveData<NotificationComponentModel> notificationComponentModelMutableLiveData;

    public NotificationViewModel() {
        notificationComponentModelMutableLiveData = null;
        notificationRepository = NotificationRepository.getInstance();
    }

    public void initData(NotificationActivity notificationActivity) {
        notificationComponentModelMutableLiveData = notificationRepository.showNotificationRepository(notificationActivity);
    }


    public MutableLiveData<NotificationComponentModel> getNotificationData() {
        return notificationComponentModelMutableLiveData;
    }

    public void removeNotifications(ArrayList<NotificationResponse> removeNotificationFromFireBase) {
     /* String response =*/   notificationRepository.removeNotificationRepository(removeNotificationFromFireBase);
//      return response;
    }
}

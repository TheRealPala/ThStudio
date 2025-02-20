package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.domainModel.Notification;

import java.util.List;

public class NotificationController {
    private final NotificationDao notificationDao;

    public NotificationController(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public List<Notification> getNotificationsByReceiverId(int receiverId) throws Exception {
        return notificationDao.getNotificationsByReceiverId(receiverId);
    }
}

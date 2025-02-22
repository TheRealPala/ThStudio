package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Notification;

import java.util.List;

public class NotificationController {
    private final NotificationDao notificationDao;
    private final PersonDao personDao;

    public NotificationController(NotificationDao notificationDao, PersonDao personDao) {
        this.notificationDao = notificationDao;
        this.personDao = personDao;
    }

    public List<Notification> getNotificationsByReceiverId(int receiverId) throws Exception {
        personDao.get(receiverId);
        return notificationDao.getNotificationsByReceiverId(receiverId);
    }
}

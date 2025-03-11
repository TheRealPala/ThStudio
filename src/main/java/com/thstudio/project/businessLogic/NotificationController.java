package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.security.AuthorizedController;
import com.thstudio.project.security.LoginController;

import java.util.List;

public class NotificationController extends AuthorizedController {
    private final NotificationDao notificationDao;
    private final PersonDao personDao;

    public NotificationController(NotificationDao notificationDao, PersonDao personDao) throws Exception {
        this.notificationDao = notificationDao;
        this.personDao = personDao;
    }

    public List<Notification> getNotificationsByReceiverId(int receiverId, String token) throws Exception {
        this.validateToken(token);
        personDao.get(receiverId);
        return notificationDao.getNotificationsByReceiverId(receiverId);
    }
}

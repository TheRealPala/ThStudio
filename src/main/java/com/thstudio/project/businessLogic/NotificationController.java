package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.security.Authz;
import com.thstudio.project.security.JwtService;

import java.util.List;

public class NotificationController {
    private final NotificationDao notificationDao;
    private final PersonDao personDao;
    private final Authz authz;

    public NotificationController(NotificationDao notificationDao, PersonDao personDao) throws Exception {
        this.notificationDao = notificationDao;
        this.personDao = personDao;
        this.authz = new Authz(new JwtService());
    }

    public List<Notification> getNotificationsByReceiverId(int receiverId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "customer", "admin");
        personDao.get(receiverId);
        return notificationDao.getNotificationsByReceiverId(receiverId);
    }
}

package businessLogic;

import dao.NotificationDao;
import domainModel.Notification;

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

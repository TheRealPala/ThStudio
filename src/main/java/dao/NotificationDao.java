package dao;

import domainModel.Notification;

import java.util.List;

public interface NotificationDao extends DAO<Notification, Integer>{
    public List<Notification> getNotificationsByReceiverId(int id) throws Exception;
}

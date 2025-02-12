package be.kdg.service;


import be.kdg.domain.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();

    Notification getNotificationById(Long id);

    List<Notification> getNotificationsByUserId(Long userId);

    List<Notification> getUnreadNotificationsByUserId(Long userId);

    Notification saveNotification(Notification notification);

    Notification markAsRead(Long id);

    void deleteNotificationById(Long id);
}
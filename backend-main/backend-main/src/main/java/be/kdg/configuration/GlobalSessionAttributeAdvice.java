package be.kdg.configuration;

import be.kdg.domain.Notification;
import be.kdg.domain.User;
import be.kdg.service.NotificationService;
import be.kdg.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalSessionAttributeAdvice {
    private final NotificationService notificationService;
    private final UserService userService;

    public GlobalSessionAttributeAdvice(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @ModelAttribute("username")
    public String getUsernameFromSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                return authentication.getName();
            }
        }
        return null;
    }

    @ModelAttribute("userId")
    public Long getUserIdFromSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                Optional<User> user = userService.getUserByUsername(authentication.getName());
                if(user.isPresent()){
                    return user.get().getId();
                }

            }
        }
        return null;
    }

    @ModelAttribute("notifications")
    public void globalAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("notifications", Collections.emptyList());
            model.addAttribute("notificationCount", 0);
            return;
        }

        String username = authentication.getName();
        Optional<User> user = userService.findByName(username);

        if (user.isEmpty()) {
            model.addAttribute("notifications", Collections.emptyList());
            model.addAttribute("notificationCount", 0);
            return;
        }

        List<Notification> notifications = notificationService.getNotificationsByUserId(user.get().getId());

        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationCount", (int) notifications.stream().filter(notification -> !notification.isRead()).count());
    }
}
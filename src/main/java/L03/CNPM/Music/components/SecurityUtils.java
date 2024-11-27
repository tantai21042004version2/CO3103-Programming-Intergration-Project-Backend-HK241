package L03.CNPM.Music.components;

import L03.CNPM.Music.models.User;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class SecurityUtils {
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.getPrincipal() instanceof User selectedUser) {
            if (!selectedUser.isActive()) {
                return null;
            }
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}

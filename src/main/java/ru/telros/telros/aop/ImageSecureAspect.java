package ru.telros.telros.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.telros.telros.exception.AccessDeniedException;
import ru.telros.telros.model.User;
import ru.telros.telros.service.UserService;
import ru.telros.telros.util.Role;

@Aspect
@Component
@RequiredArgsConstructor
/**
 * Реализует логику предотвращения доступа пользователя к логике по работе с изображениями под чужим id
 */
public class ImageSecureAspect {
    private final UserService userService;

    @Before(value = "execution(* ru.telros.telros.service.ImageServiceImpl.*(long,..)) && args(userId,..)",
            argNames = "userId")
    public void protectAnotherUserImages(long userId) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && currentUser.getId() != userId) {
            throw new AccessDeniedException(
                    "Попытка пользователя с id = %d доступа к логике по работе с изображениями под чужим id = %d"
                            .formatted(currentUser.getId(), userId));
        }
    }
}

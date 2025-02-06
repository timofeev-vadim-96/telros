package ru.telros.telros.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.exception.AccessDeniedException;
import ru.telros.telros.model.User;
import ru.telros.telros.service.UserService;
import ru.telros.telros.util.Role;

/**
 * Реализует логику предотвращения доступа пользователя к чужим данным
 */
@Aspect
@Component
@RequiredArgsConstructor
public class UserSecureAspect {
    private final UserService userService;

    @Before(value = "execution(* ru.telros.telros.service.UserInfoServiceImpl.get(long)) && args(id)",
            argNames = "id")
    public void protectGet(long id) {
        checkId(id);
    }

    @Before(value = "execution(* ru.telros.telros.service.UserInfoServiceImpl.update(..)) && args(dto)",
            argNames = "dto")
    public void protectUpdate(Object dto) {
        if (dto instanceof UserInfoViewDto userInfoViewDto) {
            checkId(userInfoViewDto.getId());
        }
    }

        @Before(value = "execution(* ru.telros.telros.service.UserInfoServiceImpl.addPhoneNumber(..)) && args(dto)",
            argNames = "dto")
    public void protectAddPhoneNumber(Object dto) {
        if (dto instanceof PhoneNumberDto phoneNumberDto) {
            checkId(phoneNumberDto.getUserId());
        }
    }


    @Before(value = "execution(* ru.telros.telros.service.UserInfoServiceImpl.deletePhoneNumber(long,long)) " +
            "&& args(userId, phoneNumberId)",
            argNames = "userId, phoneNumberId")
    public void protectDeletePhoneNumber(long userId, long phoneNumberId) {
        checkId(userId);
    }

    @Before(value = "execution(* ru.telros.telros.service.UserInfoServiceImpl.deleteById(long)) " +
            "&& args(id)",
            argNames = "id")
    public void protectDeleteById(long id) {
        checkId(id);
    }

    private void checkId(long targetUserId) {
        System.out.println("СРАБОТКА");
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) && currentUser.getId() != targetUserId) {
            throw new AccessDeniedException(
                    "Попытка пользователя с id = %d доступа к данным пользователя с id = %d"
                            .formatted(currentUser.getId(), targetUserId));
        }
    }
}

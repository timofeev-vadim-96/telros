package ru.telros.telros.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.converter.UserConverter;
import ru.telros.telros.exception.EntityNotFoundException;
import ru.telros.telros.exception.PhoneNumberAlreadyExistsException;
import ru.telros.telros.model.PhoneNumber;
import ru.telros.telros.model.User;
import ru.telros.telros.repository.PhoneNumberDao;
import ru.telros.telros.repository.SearchCriteriaWithPaginationUserDao;
import ru.telros.telros.repository.UserDao;
import ru.telros.telros.service.dto.UserInfoDto;
import ru.telros.telros.util.SearchCriteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserDao userDao;

    private final SearchCriteriaWithPaginationUserDao userCriteriaDao;

    private final UserConverter userConverter;

    private final PhoneNumberDao phoneNumberDao;

    /**
     * Получение пользователя по идентификатору
     *
     * @param id идентификатор
     * @return пользователя
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userById", key = "#id")
    public UserInfoDto get(long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = %d is not found".formatted(id)));
        return userConverter.convertToDto(user);
    }

    /**
     * Получение пользователей с пагинацией и фильтрацией
     *
     * @param firstName   имя
     * @param secondName  фамилия
     * @param patronymic  отчество
     * @param birthDay    дата рождения
     * @param phoneNumber номер телефона
     * @param pageable    параметры пагинации
     * @return список пользователей, соответствующий критериям поиска
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable("users")
    public Page<UserInfoDto> getAll(String firstName, String secondName, String patronymic, LocalDate birthDay,
                                    String phoneNumber, Pageable pageable) {
        List<SearchCriteria> criteria =
                collectSearchCriteriaParams(firstName, secondName, patronymic, birthDay, phoneNumber);

        Page<User> users = userCriteriaDao.findAll(criteria, pageable);

        return new PageImpl<>(
                userConverter.convertToDtos(users.getContent()),
                pageable,
                users.getTotalElements()
        );
    }

    /**
     * Обновление пользовательской информации
     *
     * @param dto пользовательская информация для обновления
     * @return обновленного пользователя
     */
    @Override
    @Transactional
    @Caching(
            evict = {@CacheEvict(value = "users", allEntries = true)},
            put = {@CachePut(value = "userById", key = "#result.id")})
    public UserInfoDto update(UserInfoViewDto dto) {
        User user = userDao.findById(dto.getId())
                .orElseThrow(()
                        -> new EntityNotFoundException("User with id = %d is not found".formatted(dto.getId())));
        updateEntityFromDto(user, dto);

        User updated = userDao.save(user);

        return userConverter.convertToDto(updated);
    }

    @Override
    @Transactional
    @Caching(
            evict = {@CacheEvict(value = "users", allEntries = true)},
            put = {@CachePut(value = "userById", key = "#result.id")})
    public UserInfoDto addPhoneNumber(PhoneNumberDto dto) {
        User user = userDao.findById(dto.getUserId())
                .orElseThrow(()
                        -> new EntityNotFoundException("User with id = %d is not found".formatted(dto.getId())));

        Optional<PhoneNumber> number = phoneNumberDao.findPhoneNumberByPhoneNumber(dto.getPhoneNumber());
        if (number.isPresent()) {
            throw new PhoneNumberAlreadyExistsException("Phone number = %s is already exists"
                    .formatted(dto.getPhoneNumber()));
        }

        PhoneNumber newPhoneNumber = PhoneNumber.builder()
                .phoneNumber(dto.getPhoneNumber())
                .user(user)
                .build();
        PhoneNumber saved = phoneNumberDao.save(newPhoneNumber);

        user.getPhoneNumbers().add(saved);
        User updated = userDao.save(user);

        return userConverter.convertToDto(updated);
    }

    @Override
    @Transactional
    @Caching(
            evict = {@CacheEvict(value = "users", allEntries = true)},
            put = {@CachePut(value = "userById", key = "#result.id")})
    public UserInfoDto deletePhoneNumber(long userId, long phoneNumberId) {
        User user = userDao.findById(userId)
                .orElseThrow(()
                        -> new EntityNotFoundException("User with id = %d is not found".formatted(userId)));
        user.getPhoneNumbers().removeIf(numb -> numb.getId() == phoneNumberId);

        User updated = userDao.save(user);

        return userConverter.convertToDto(updated);
    }

    /**
     * Удаление пользователя по идентификатору
     *
     * @param id идентификатор
     */
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "userById", key = "#id")})
    public void deleteById(long id) {
        userDao.deleteById(id);
    }

    /**
     * Составитель списка критериев по фильтрации пользователей по полям
     *
     * @param firstName   имя
     * @param secondName  фамилия
     * @param patronymic  отчество
     * @param birthDay    дата рождения
     * @param phoneNumber номер телефона
     * @return список критериев фильтрации
     */
    private List<SearchCriteria> collectSearchCriteriaParams(String firstName, String secondName, String patronymic,
                                                             LocalDate birthDay, String phoneNumber) {
        List<SearchCriteria> params = new ArrayList<>();
        if (firstName != null) {
            params.add(new SearchCriteria("firstName", ":", firstName.trim()));
        }
        if (secondName != null) {
            params.add(new SearchCriteria("secondName", ":", secondName.trim()));
        }
        if (patronymic != null) {
            params.add(new SearchCriteria("patronymic", ":", patronymic.trim()));
        }
        if (patronymic != null) {
            params.add(new SearchCriteria("patronymic", ":", patronymic.trim()));
        }
        if (birthDay != null) {
            params.add(new SearchCriteria("birthDay", ":", birthDay));
        }
        if (phoneNumber != null) {
            params.add(new SearchCriteria("phoneNumber", ":", phoneNumber.trim()));
        }

        return params;
    }

    /**
     * Обновляет сущность на базе дто без обновления email и номеров телефонов
     *
     * @param entity сущность
     * @param dto    дто
     */
    private void updateEntityFromDto(User entity, UserInfoViewDto dto) {
        if (dto.getFirstName() != null) {
            entity.setFirstName(dto.getFirstName());
        }
        if (dto.getSecondName() != null) {
            entity.setSecondName(dto.getSecondName());
        }
        if (dto.getPatronymic() != null) {
            entity.setPatronymic(dto.getPatronymic());
        }
        if (dto.getBirthDay() != null) {
            entity.setBirthDay(dto.getBirthDay());
        }
    }
}

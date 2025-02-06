package ru.telros.telros.converter;

import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.model.PhoneNumber;

import java.util.Set;

public interface PhoneNumberConverter {
    PhoneNumberDto convertToDto(PhoneNumber entity);

    Set<PhoneNumberDto> convertToDtos(Set<PhoneNumber> entities);
}

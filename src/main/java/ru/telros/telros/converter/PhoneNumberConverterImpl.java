package ru.telros.telros.converter;

import org.springframework.stereotype.Component;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.model.PhoneNumber;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PhoneNumberConverterImpl implements PhoneNumberConverter {

    @Override
    public PhoneNumberDto convertToDto(PhoneNumber entity) {
        return PhoneNumberDto.builder()
                .id(entity.getId())
                .phoneNumber(entity.getPhoneNumber())
                .userId(entity.getId())
                .build();
    }

    @Override
    public Set<PhoneNumberDto> convertToDtos(Set<PhoneNumber> entities) {
        return entities.stream().map(this::convertToDto).collect(Collectors.toSet());
    }
}

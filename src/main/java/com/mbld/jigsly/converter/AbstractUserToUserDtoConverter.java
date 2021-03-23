package com.mbld.jigsly.converter;

import com.mbld.jigsly.dto.UserDto;
import com.mbld.jigsly.model.user.AbstractUser;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class AbstractUserToUserDtoConverter implements Converter<AbstractUser, UserDto> {

    @Synchronized
    @Nullable
    @Override
    public UserDto convert(AbstractUser user) {
        return UserDto.builder().
                username(user.getUsername())
                .authorities(user.getAuthorities())
                .role(user.getRole())
                .isActive(user.isActive()).build();
    }
}

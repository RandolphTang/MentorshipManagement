package com.example.securityOAuth.security.services.impl;

import com.example.securityOAuth.dto.SignUpDto;
import com.example.securityOAuth.dto.UserDto;
import com.example.securityOAuth.entity.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(UserEntity user);


    @Mapping(target = "password", ignore = true)
    UserEntity signUpToUser(SignUpDto userDto);
}

package com.example.trainstationtemp.dto;

import java.util.List;

public record UserDto(
        String username,
        String password,
        List<String> authorities
) {
}

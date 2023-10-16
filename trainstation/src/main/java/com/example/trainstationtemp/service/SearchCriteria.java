package com.example.trainstationtemp.service;


public record SearchCriteria (
        String key,
        String operation,
        Object value
) {
}
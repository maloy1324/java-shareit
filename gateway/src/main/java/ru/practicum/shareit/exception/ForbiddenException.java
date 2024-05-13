package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ForbiddenException extends RuntimeException {
    public final String message;
}

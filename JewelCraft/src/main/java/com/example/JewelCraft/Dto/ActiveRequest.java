package com.example.JewelCraft.Dto;

import jakarta.validation.constraints.NotNull;

/** Turns something on/off: product availability, user account enabled. */
public record ActiveRequest(@NotNull(message = "active is required") Boolean active) {
}

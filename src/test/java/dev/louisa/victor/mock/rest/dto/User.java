package dev.louisa.victor.mock.rest.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record User(UUID id, String name) {
}

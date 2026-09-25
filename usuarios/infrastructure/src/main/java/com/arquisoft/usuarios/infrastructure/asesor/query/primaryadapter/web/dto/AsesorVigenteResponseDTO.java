package com.arquisoft.usuarios.infrastructure.asesor.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AsesorVigenteResponseDTO(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado
) {
}

package com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AsesorFichaVigenteResponseDTO(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado
) {
}

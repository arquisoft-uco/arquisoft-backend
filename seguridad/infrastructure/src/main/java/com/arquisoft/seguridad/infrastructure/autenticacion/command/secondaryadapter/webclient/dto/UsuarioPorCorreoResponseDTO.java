package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioPorCorreoResponseDTO(String id) {
}

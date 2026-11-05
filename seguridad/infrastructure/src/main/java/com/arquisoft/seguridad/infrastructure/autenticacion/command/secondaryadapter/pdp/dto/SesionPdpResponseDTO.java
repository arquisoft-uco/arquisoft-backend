package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SesionPdpResponseDTO(Datos data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Datos(String subject, String name, String email, String tenantId) {}
}

package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FichaPerfilResponseDTO(
        UUID id,
        String tituloProyecto,
        AsesorFichaResponseDTO asesorFicha
) {
}

package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto;

import com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionParticipanteResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FichaPerfilRepresentanteResponseDTO(
        UUID id,
        String tituloProyecto,
        AsesorFichaResponseDTO asesorFicha,
        EstadoFichaPerfilResponseDTO estado,
        EvaluacionParticipanteResponseDTO evaluacion
) {
}

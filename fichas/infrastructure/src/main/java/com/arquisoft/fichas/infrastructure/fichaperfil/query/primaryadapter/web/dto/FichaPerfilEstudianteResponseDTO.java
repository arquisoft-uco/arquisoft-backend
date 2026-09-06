package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto;

import com.arquisoft.fichas.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.dto.EstudianteFichaPerfilResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FichaPerfilEstudianteResponseDTO(
        UUID idFichaPerfil,
        String titulo,
        AsesorFichaResponseDTO asesor,
        EstadoFichaPerfilResponseDTO estado,
        List<EstudianteFichaPerfilResponseDTO> estudiantes
) {
}

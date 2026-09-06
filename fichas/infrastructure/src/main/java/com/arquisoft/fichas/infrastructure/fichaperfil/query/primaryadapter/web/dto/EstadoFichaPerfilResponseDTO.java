package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto;

import java.time.Instant;

public record EstadoFichaPerfilResponseDTO(
        String id,
        String nombre,
        Instant fechaActualizacion
) {
}

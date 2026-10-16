package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MapaRutaResponseDTO(
        UUID id,
        UUID proyectoGrado,
        String tituloProyecto,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}

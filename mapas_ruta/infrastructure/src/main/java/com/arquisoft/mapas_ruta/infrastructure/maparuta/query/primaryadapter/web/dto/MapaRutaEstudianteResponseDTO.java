package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MapaRutaEstudianteResponseDTO(
        UUID id,
        UUID proyectoGrado,
        String tituloProyecto,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}

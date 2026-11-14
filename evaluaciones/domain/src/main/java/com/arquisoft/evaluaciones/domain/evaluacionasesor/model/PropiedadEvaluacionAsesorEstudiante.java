package com.arquisoft.evaluaciones.domain.evaluacionasesor.model;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public record PropiedadEvaluacionAsesorEstudiante(
        UUID evaluacionAsesor,
        List<UUID> estudiantesVinculados,
        UUID estudiante
) {

    public PropiedadEvaluacionAsesorEstudiante {
        estudiantesVinculados = UtilColeccion.aplicarPorDefecto(estudiantesVinculados);
    }
}

package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;

import java.util.List;
import java.util.UUID;

public interface ConsultarObservacionesItemAsesorEstudianteValidator {

    void validar(
            UUID evaluacionCuantitativaAsesor,
            AccesoEvaluacionCuantitativaAsesorDomain acceso,
            List<UUID> estudiantesVinculados,
            UUID estudianteSolicitante);
}

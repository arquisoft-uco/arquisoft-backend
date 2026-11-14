package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.validator;

import java.util.List;
import java.util.UUID;

public interface ConsultarEvaluacionesCualitativasAsesorEstudianteValidator {

    void validar(UUID evaluacionAsesor, UUID asesorAsignado, List<UUID> estudiantesVinculados, UUID estudiante);
}

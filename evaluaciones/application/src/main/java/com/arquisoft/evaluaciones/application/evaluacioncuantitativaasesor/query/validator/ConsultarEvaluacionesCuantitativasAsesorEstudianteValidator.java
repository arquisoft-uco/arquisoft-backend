package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.validator;

import java.util.List;
import java.util.UUID;

public interface ConsultarEvaluacionesCuantitativasAsesorEstudianteValidator {

    void validar(UUID evaluacionAsesor, UUID asesorAsignado, List<UUID> estudiantesVinculados, UUID estudiante);
}

package com.arquisoft.evaluaciones.application.observacionitemasesor.query.validator;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.AccesoEvaluacionCuantitativaAsesorDomain;

import java.util.UUID;

public interface ConsultarObservacionesItemAsesorValidator {

    void validar(
            UUID evaluacionCuantitativaAsesor,
            AccesoEvaluacionCuantitativaAsesorDomain acceso,
            UUID asesorSolicitante);
}

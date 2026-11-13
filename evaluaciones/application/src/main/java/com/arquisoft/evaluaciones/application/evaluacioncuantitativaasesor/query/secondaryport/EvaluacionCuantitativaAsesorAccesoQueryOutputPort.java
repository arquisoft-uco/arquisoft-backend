package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorAccesoQueryOutputPort {

    Optional<EvaluacionCuantitativaAsesorAccesoEntity> obtenerAccesoPorId(UUID evaluacionCuantitativaAsesor);
}

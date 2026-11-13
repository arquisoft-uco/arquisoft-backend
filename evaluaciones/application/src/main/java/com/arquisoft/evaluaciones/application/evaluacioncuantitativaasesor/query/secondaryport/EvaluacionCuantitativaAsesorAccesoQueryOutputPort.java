package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.entity.EvaluacionCuantitativaAsesorAccesoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorAccesoQueryOutputPort {

    Optional<EvaluacionCuantitativaAsesorAccesoEntity> obtenerAccesoPorId(UUID evaluacionCuantitativaAsesor);

    List<UUID> obtenerEstudiantesVigentesPorId(UUID evaluacionCuantitativaAsesor);
}

package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCualitativaAsesorEstudianteQueryOutputPort {

    List<EvaluacionCualitativaAsesorEstudianteReadModel> consultarPorEvaluacionAsesor(UUID evaluacionAsesor);
}

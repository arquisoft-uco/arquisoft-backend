package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorQueryOutputPort {

    List<EvaluacionCuantitativaAsesorReadModel> consultar(EvaluacionCuantitativaAsesorCriteria criteria);

    List<EvaluacionCuantitativaAsesorReadModel> consultarPorEvaluacionAsesor(UUID evaluacionAsesor);
}

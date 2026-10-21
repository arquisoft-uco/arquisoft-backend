package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;

import java.util.List;

public interface EvaluacionCualitativaAsesorQueryOutputPort {

    List<EvaluacionCualitativaAsesorReadModel> consultar(EvaluacionCualitativaAsesorCriteria criteria);
}

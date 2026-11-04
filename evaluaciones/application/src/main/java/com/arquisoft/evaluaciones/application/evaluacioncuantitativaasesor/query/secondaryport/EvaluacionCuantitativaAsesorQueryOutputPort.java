package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;

import java.util.List;

public interface EvaluacionCuantitativaAsesorQueryOutputPort {

    List<EvaluacionCuantitativaAsesorReadModel> consultar(EvaluacionCuantitativaAsesorCriteria criteria);
}

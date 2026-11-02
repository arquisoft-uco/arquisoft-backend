package com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;

public interface EvaluacionAsesorDeEvaluacionQueryOutputPort {

    EvaluacionAsesorReadModel consultar(EvaluacionAsesorDeEvaluacionCriteria criteria);
}

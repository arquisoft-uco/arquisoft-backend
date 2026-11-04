package com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;

public interface EvaluacionAsesorRealizadaQueryOutputPort {

    EvaluacionAsesorRealizadaReadModel consultar(EvaluacionAsesorRealizadaCriteria criteria);
}

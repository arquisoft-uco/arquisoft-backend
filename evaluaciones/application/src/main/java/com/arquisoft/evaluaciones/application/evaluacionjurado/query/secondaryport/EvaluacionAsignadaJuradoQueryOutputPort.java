package com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;

import java.util.List;

public interface EvaluacionAsignadaJuradoQueryOutputPort {

    List<EvaluacionAsignadaJuradoReadModel> consultar(EvaluacionesAsignadasJuradoCriteria criteria);
}

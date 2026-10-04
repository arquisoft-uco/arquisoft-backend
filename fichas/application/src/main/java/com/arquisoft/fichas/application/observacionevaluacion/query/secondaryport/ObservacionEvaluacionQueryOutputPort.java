package com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;

import java.util.List;

public interface ObservacionEvaluacionQueryOutputPort {

    List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYEstudiante(
            ObservacionEvaluacionEstudianteCriteria criteria);
}

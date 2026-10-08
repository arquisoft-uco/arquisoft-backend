package com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;

import java.util.List;

public interface ObservacionEvaluacionQueryOutputPort {

    List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYEstudiante(
            ObservacionEvaluacionEstudianteCriteria criteria);

    List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYAsesorFicha(
            ObservacionEvaluacionAsesorCriteria criteria);

    List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYRepresentanteComite(
            ObservacionEvaluacionRepresentanteCriteria criteria);
}

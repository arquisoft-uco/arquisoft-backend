package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper.ObservacionEvaluacionEstudianteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObservacionEvaluacionQueryOutputAdapter implements ObservacionEvaluacionQueryOutputPort {

    private final ObservacionEvaluacionEstudianteQueryRepository observacionEvaluacionEstudianteQueryRepository;

    @Override
    public List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYEstudiante(
            ObservacionEvaluacionEstudianteCriteria criteria) {
        return observacionEvaluacionEstudianteQueryRepository
                .findByEvaluacionFichaPerfilIdAndEstudianteIdOrderByObservacionAscIdAsc(
                        criteria.evaluacionFichaPerfil(), criteria.estudiante())
                .stream()
                .map(ObservacionEvaluacionEstudianteQueryMapper::toReadModel)
                .toList();
    }
}

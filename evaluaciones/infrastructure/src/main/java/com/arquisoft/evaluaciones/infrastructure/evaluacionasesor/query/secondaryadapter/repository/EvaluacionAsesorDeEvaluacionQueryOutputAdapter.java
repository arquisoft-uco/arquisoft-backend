package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorDeEvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception.EvaluacionAsesorNoDisponibleException;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper.EvaluacionAsesorDeEvaluacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorDeEvaluacionQueryOutputAdapter implements EvaluacionAsesorDeEvaluacionQueryOutputPort {

    private final EvaluacionAsesorDeEvaluacionQueryRepository repository;

    @Override
    public EvaluacionAsesorReadModel consultar(EvaluacionAsesorDeEvaluacionCriteria criteria) {
        return repository.findByEvaluacionId(criteria.evaluacion())
                .map(EvaluacionAsesorDeEvaluacionQueryMapper::toReadModel)
                .orElseThrow(() -> new EvaluacionAsesorNoDisponibleException(criteria.evaluacion()));
    }
}

package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorRealizadaQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.exception.EvaluacionAsesorRealizadaNoDisponibleException;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper.EvaluacionAsesorRealizadaQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorRealizadaQueryOutputAdapter implements EvaluacionAsesorRealizadaQueryOutputPort {

    private final EvaluacionAsesorRealizadaQueryRepository repository;

    @Override
    public EvaluacionAsesorRealizadaReadModel consultar(EvaluacionAsesorRealizadaCriteria criteria) {
        return repository.findById(criteria.evaluacionAsesor())
                .map(EvaluacionAsesorRealizadaQueryMapper::toReadModel)
                .orElseThrow(() -> new EvaluacionAsesorRealizadaNoDisponibleException(criteria.evaluacionAsesor()));
    }
}

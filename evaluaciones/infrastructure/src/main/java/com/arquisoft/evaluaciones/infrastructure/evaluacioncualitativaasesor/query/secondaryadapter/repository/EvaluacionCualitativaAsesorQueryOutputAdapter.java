package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.mapper.EvaluacionCualitativaAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaAsesorQueryOutputAdapter implements EvaluacionCualitativaAsesorQueryOutputPort {

    private final EvaluacionCualitativaAsesorQueryRepository repository;

    @Override
    public List<EvaluacionCualitativaAsesorReadModel> consultar(EvaluacionCualitativaAsesorCriteria criteria) {
        return repository.findByEvaluacionAsesorIdOrderByItemNombreAscIdAsc(criteria.evaluacionAsesor())
                .stream()
                .map(EvaluacionCualitativaAsesorQueryMapper::toReadModel)
                .toList();
    }
}

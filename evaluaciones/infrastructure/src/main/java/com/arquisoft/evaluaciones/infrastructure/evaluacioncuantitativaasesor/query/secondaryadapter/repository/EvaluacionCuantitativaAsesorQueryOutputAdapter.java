package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.secondaryport.EvaluacionCuantitativaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.mapper.EvaluacionCuantitativaAsesorQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaAsesorQueryOutputAdapter implements EvaluacionCuantitativaAsesorQueryOutputPort {

    private final EvaluacionCuantitativaAsesorQueryRepository repository;

    @Override
    public List<EvaluacionCuantitativaAsesorReadModel> consultar(EvaluacionCuantitativaAsesorCriteria criteria) {
        return consultarPorEvaluacionAsesor(criteria.evaluacionAsesor());
    }

    @Override
    public List<EvaluacionCuantitativaAsesorReadModel> consultarPorEvaluacionAsesor(UUID evaluacionAsesor) {
        return repository.findByEvaluacionAsesorIdOrderByItemNombreAscIdAsc(evaluacionAsesor)
                .stream()
                .map(EvaluacionCuantitativaAsesorQueryMapper::toReadModel)
                .toList();
    }
}

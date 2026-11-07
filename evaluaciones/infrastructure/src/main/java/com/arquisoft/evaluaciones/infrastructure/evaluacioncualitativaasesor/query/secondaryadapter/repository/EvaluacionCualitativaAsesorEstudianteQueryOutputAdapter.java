package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.secondaryport.EvaluacionCualitativaAsesorEstudianteQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.mapper.EvaluacionCualitativaAsesorEstudianteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaAsesorEstudianteQueryOutputAdapter
        implements EvaluacionCualitativaAsesorEstudianteQueryOutputPort {

    private final EvaluacionCualitativaAsesorEstudianteQueryRepository repository;

    @Override
    public List<EvaluacionCualitativaAsesorEstudianteReadModel> consultarPorEvaluacionAsesor(UUID evaluacionAsesor) {
        return repository.findByEvaluacionAsesorIdOrderByItemNombreAscIdAsc(evaluacionAsesor)
                .stream()
                .map(EvaluacionCualitativaAsesorEstudianteQueryMapper::toReadModel)
                .toList();
    }
}

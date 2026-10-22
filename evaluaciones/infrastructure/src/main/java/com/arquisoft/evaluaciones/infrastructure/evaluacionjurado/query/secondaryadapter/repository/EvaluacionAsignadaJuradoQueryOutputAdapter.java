package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.mapper.EvaluacionAsignadaJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluacionAsignadaJuradoQueryOutputAdapter implements EvaluacionAsignadaJuradoQueryOutputPort {

    private final EvaluacionAsignadaJuradoQueryRepository repository;

    @Override
    public List<EvaluacionAsignadaJuradoReadModel> consultar(EvaluacionesAsignadasJuradoCriteria criteria) {
        return repository
                .findByJuradoIdOrderByEstadoOrdenAscEntregableProyectoAscEntregableVersionAscIdAsc(
                        criteria.jurado())
                .stream()
                .map(EvaluacionAsignadaJuradoQueryMapper::toReadModel)
                .toList();
    }
}

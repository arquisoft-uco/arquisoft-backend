package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.secondaryport.EvaluacionCualitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository.mapper.EvaluacionCualitativaJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluacionCualitativaJuradoQueryOutputAdapter implements EvaluacionCualitativaJuradoQueryOutputPort {

    private final EvaluacionCualitativaJuradoQueryRepository repository;

    @Override
    public List<EvaluacionCualitativaJuradoReadModel> consultar(EvaluacionCualitativaJuradoCriteria criteria) {
        return repository.findByEvaluacionJuradoIdAndEstudianteIdOrderByItemNombreAscIdAsc(
                        criteria.evaluacionJuradoId(), criteria.estudianteId())
                .stream()
                .map(EvaluacionCualitativaJuradoQueryMapper::toReadModel)
                .toList();
    }
}

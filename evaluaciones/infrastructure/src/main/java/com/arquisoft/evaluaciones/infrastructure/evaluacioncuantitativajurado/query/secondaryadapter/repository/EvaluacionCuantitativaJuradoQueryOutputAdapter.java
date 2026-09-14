package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.secondaryport.EvaluacionCuantitativaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository.mapper.EvaluacionCuantitativaJuradoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EvaluacionCuantitativaJuradoQueryOutputAdapter implements EvaluacionCuantitativaJuradoQueryOutputPort {

    private final EvaluacionCuantitativaJuradoQueryRepository repository;

    @Override
    public List<EvaluacionCuantitativaJuradoReadModel> consultar(EvaluacionCuantitativaJuradoCriteria criteria) {
        return repository.findByEvaluacionJuradoIdAndEstudianteIdOrderByItemNombreAscIdAsc(
                        criteria.evaluacionJuradoId(), criteria.estudianteId())
                .stream()
                .map(EvaluacionCuantitativaJuradoQueryMapper::toReadModel)
                .toList();
    }
}

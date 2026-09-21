package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.mapper.EvaluacionJuradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoQueryOutputAdapter implements EvaluacionJuradoQueryOutputPort {

    private final EvaluacionJuradoQueryRepository evaluacionJuradoQueryRepository;
    private final EvaluacionJuradoJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionJuradoReadModel> consultarTodas(EvaluacionJuradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EvaluacionJuradoSortMapper::traducir);
        var spec = specification.deLaEvaluacion(criteria.getEvaluacion())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                evaluacionJuradoQueryRepository.findAll(spec, pageable)
                        .map(EvaluacionJuradoQueryMapper::toReadModel));
    }
}

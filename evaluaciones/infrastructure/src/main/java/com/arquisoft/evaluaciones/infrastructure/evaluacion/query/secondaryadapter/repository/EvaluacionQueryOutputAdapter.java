package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.secondaryport.EvaluacionQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper.EvaluacionQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionQueryOutputAdapter implements EvaluacionQueryOutputPort {

    private final EvaluacionQueryRepository evaluacionQueryRepository;
    private final EvaluacionJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionReadModel> consultarTodas(EvaluacionCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EvaluacionSortMapper::traducir);

        return PaginationMapper.toResult(
                evaluacionQueryRepository.findAll(specification.desdeCriteria(criteria), pageable)
                        .map(EvaluacionQueryMapper::toReadModel));
    }
}

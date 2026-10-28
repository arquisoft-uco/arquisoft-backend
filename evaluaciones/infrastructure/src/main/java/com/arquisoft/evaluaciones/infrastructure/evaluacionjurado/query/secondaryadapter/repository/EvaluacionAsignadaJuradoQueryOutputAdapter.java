package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionAsignadaJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.mapper.EvaluacionAsignadaJuradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionAsignadaJuradoQueryOutputAdapter implements EvaluacionAsignadaJuradoQueryOutputPort {

    private final EvaluacionAsignadaJuradoQueryRepository repository;
    private final EvaluacionAsignadaJuradoJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionAsignadaJuradoReadModel> consultarTodas(
            EvaluacionesAsignadasJuradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EvaluacionAsignadaJuradoSortMapper::traducir);
        var spec = specification.delJurado(criteria.getJurado())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                repository.findAll(spec, pageable)
                        .map(EvaluacionAsignadaJuradoQueryMapper::toReadModel));
    }
}

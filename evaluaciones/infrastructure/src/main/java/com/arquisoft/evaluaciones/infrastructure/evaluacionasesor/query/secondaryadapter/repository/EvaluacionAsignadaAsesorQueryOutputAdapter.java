package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsignadaAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper.EvaluacionAsignadaAsesorQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionAsignadaAsesorQueryOutputAdapter implements EvaluacionAsignadaAsesorQueryOutputPort {

    private final EvaluacionAsignadaAsesorQueryRepository repository;
    private final EvaluacionAsignadaAsesorJpaSpecification specification;

    @Override
    public PaginatedResult<EvaluacionAsignadaAsesorReadModel> consultarTodas(
            EvaluacionesAsignadasAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EvaluacionAsignadaAsesorSortMapper::traducir);
        var spec = specification.delAsesor(criteria.getAsesor())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                repository.findAll(spec, pageable)
                        .map(EvaluacionAsignadaAsesorQueryMapper::toReadModel));
    }
}

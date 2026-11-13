package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.secondaryport.ObservacionItemAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository.mapper.ObservacionItemAsesorQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionItemAsesorQueryOutputAdapter implements ObservacionItemAsesorQueryOutputPort {

    private final ObservacionItemAsesorQueryRepository observacionItemAsesorQueryRepository;
    private final ObservacionItemAsesorJpaSpecification specification;

    @Override
    public PaginatedResult<ObservacionItemAsesorReadModel> consultarTodas(ObservacionItemAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ObservacionItemAsesorSortMapper::traducir);
        var spec = specification.deLaEvaluacion(criteria.getEvaluacionCuantitativaAsesor())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                observacionItemAsesorQueryRepository.findAll(spec, pageable)
                        .map(ObservacionItemAsesorQueryMapper::toReadModel));
    }
}

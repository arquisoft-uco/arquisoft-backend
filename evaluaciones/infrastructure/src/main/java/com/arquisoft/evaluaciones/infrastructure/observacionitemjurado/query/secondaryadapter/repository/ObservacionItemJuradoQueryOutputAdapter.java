package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.readmodel.ObservacionItemJuradoReadModel;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.secondaryport.ObservacionItemJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository.mapper.ObservacionItemJuradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionItemJuradoQueryOutputAdapter implements ObservacionItemJuradoQueryOutputPort {

    private final ObservacionItemJuradoQueryRepository observacionItemJuradoQueryRepository;
    private final ObservacionItemJuradoJpaSpecification specification;

    @Override
    public PaginatedResult<ObservacionItemJuradoReadModel> consultarTodas(ObservacionItemJuradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ObservacionItemJuradoSortMapper::traducir);
        var spec = specification.deLaEvaluacion(criteria.getEvaluacionCuantitativaJurado())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                observacionItemJuradoQueryRepository.findAll(spec, pageable)
                        .map(ObservacionItemJuradoQueryMapper::toReadModel));
    }
}

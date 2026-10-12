package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.application.observacionitem.query.secondaryport.ObservacionItemQueryOutputPort;
import com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.mapper.ObservacionItemEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository.mapper.ObservacionItemQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionItemQueryOutputAdapter implements ObservacionItemQueryOutputPort {

    private static final String ID = "id";

    private final ObservacionItemQueryRepository observacionItemRepository;
    private final ObservacionItemJpaSpecification specification;
    private final ObservacionItemEstudianteQueryRepository observacionItemEstudianteRepository;
    private final ObservacionItemEstudianteJpaSpecification estudianteSpecification;

    @Override
    public PaginatedResult<ObservacionItemReadModel> consultarTodas(ObservacionItemCriteria criteria) {
        var pageable = conDesempate(
                PageableMapper.toPageable(criteria, ObservacionItemSortMapper::traducir));
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                observacionItemRepository.findAll(spec, pageable)
                        .map(ObservacionItemQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<ObservacionItemReadModel> consultarTodasEstudiante(
            ObservacionItemEstudianteCriteria criteria) {
        var pageable = conDesempate(
                PageableMapper.toPageable(criteria, ObservacionItemEstudianteSortMapper::traducir));
        var spec = estudianteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                observacionItemEstudianteRepository.findAll(spec, pageable)
                        .map(ObservacionItemEstudianteQueryMapper::toReadModel));
    }

    private static Pageable conDesempate(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().and(Sort.by(ID)));
    }
}

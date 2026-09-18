package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.application.revisionitem.query.secondaryport.RevisionItemQueryOutputPort;
import com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.mapper.RevisionItemEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.mapper.RevisionItemQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RevisionItemQueryOutputAdapter implements RevisionItemQueryOutputPort {

    private final RevisionItemQueryRepository revisionItemRepository;
    private final RevisionItemJpaSpecification specification;
    private final RevisionItemEstudianteQueryRepository revisionItemEstudianteRepository;
    private final RevisionItemEstudianteJpaSpecification estudianteSpecification;

    @Override
    public PaginatedResult<RevisionItemReadModel> consultarTodas(RevisionItemCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RevisionItemSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                revisionItemRepository.findAll(spec, pageable)
                        .map(RevisionItemQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<RevisionItemReadModel> consultarTodasEstudiante(RevisionItemEstudianteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RevisionItemEstudianteSortMapper::traducir);
        var spec = estudianteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                revisionItemEstudianteRepository.findAll(spec, pageable)
                        .map(RevisionItemEstudianteQueryMapper::toReadModel));
    }
}

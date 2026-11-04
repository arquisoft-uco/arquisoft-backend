package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.secondaryport.RepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository.mapper.RepositorioArtefactoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositorioArtefactoQueryOutputAdapter implements RepositorioArtefactoQueryOutputPort {

    private final RepositorioArtefactoQueryRepository repositorioArtefactoQueryRepository;
    private final RepositorioArtefactoJpaSpecification specification;

    @Override
    public PaginatedResult<RepositorioArtefactoReadModel> consultarTodos(RepositorioArtefactoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RepositorioArtefactoSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                repositorioArtefactoQueryRepository.findAll(spec, pageable)
                        .map(RepositorioArtefactoQueryMapper::toReadModel));
    }
}

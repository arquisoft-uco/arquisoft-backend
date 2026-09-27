package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.secondaryport.RepresentanteComiteQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository.mapper.RepresentanteComiteQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteQueryOutputAdapter implements RepresentanteComiteQueryOutputPort {

    private final RepresentanteComiteQueryRepository representanteComiteQueryRepository;
    private final RepresentanteComiteVigenteQueryRepository representanteComiteVigenteQueryRepository;
    private final RepresentanteComiteJpaSpecification representanteComiteSpecification;
    private final RepresentanteComiteVigenteJpaSpecification representanteComiteVigenteSpecification;

    @Override
    public PaginatedResult<RepresentanteComiteReadModel> consultarTodos(RepresentanteComiteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RepresentanteComiteSortMapper::traducir);
        var spec = representanteComiteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                representanteComiteQueryRepository.findAll(spec, pageable)
                        .map(RepresentanteComiteQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<RepresentanteComiteVigenteReadModel> consultarVigentes(RepresentanteComiteVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RepresentanteComiteVigenteSortMapper::traducir);
        var spec = representanteComiteVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                representanteComiteVigenteQueryRepository.findAll(spec, pageable)
                        .map(RepresentanteComiteQueryMapper::toReadModel));
    }
}

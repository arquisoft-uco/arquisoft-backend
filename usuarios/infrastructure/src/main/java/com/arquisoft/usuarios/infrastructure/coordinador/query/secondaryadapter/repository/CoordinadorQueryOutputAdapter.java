package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.application.coordinador.query.secondaryport.CoordinadorQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository.mapper.CoordinadorQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoordinadorQueryOutputAdapter implements CoordinadorQueryOutputPort {

    private final CoordinadorQueryRepository coordinadorQueryRepository;
    private final CoordinadorVigenteQueryRepository coordinadorVigenteQueryRepository;
    private final CoordinadorJpaSpecification coordinadorSpecification;
    private final CoordinadorVigenteJpaSpecification coordinadorVigenteSpecification;

    @Override
    public PaginatedResult<CoordinadorReadModel> consultarTodos(CoordinadorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, CoordinadorSortMapper::traducir);
        var spec = coordinadorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                coordinadorQueryRepository.findAll(spec, pageable)
                        .map(CoordinadorQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<CoordinadorVigenteReadModel> consultarVigentes(CoordinadorVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, CoordinadorVigenteSortMapper::traducir);
        var spec = coordinadorVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                coordinadorVigenteQueryRepository.findAll(spec, pageable)
                        .map(CoordinadorQueryMapper::toReadModel));
    }
}

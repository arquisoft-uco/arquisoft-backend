package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
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

    private final CoordinadorVigenteQueryRepository coordinadorVigenteQueryRepository;
    private final CoordinadorVigenteJpaSpecification coordinadorVigenteSpecification;

    @Override
    public PaginatedResult<CoordinadorVigenteReadModel> consultarVigentes(CoordinadorVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, CoordinadorVigenteSortMapper::traducir);
        var spec = coordinadorVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                coordinadorVigenteQueryRepository.findAll(spec, pageable)
                        .map(CoordinadorQueryMapper::toReadModel));
    }
}

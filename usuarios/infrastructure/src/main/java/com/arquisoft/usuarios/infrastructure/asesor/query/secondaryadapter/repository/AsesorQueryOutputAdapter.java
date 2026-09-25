package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.application.asesor.query.secondaryport.AsesorQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository.mapper.AsesorQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorQueryOutputAdapter implements AsesorQueryOutputPort {

    private final AsesorQueryRepository asesorQueryRepository;
    private final AsesorVigenteQueryRepository asesorVigenteQueryRepository;
    private final AsesorJpaSpecification asesorSpecification;
    private final AsesorVigenteJpaSpecification asesorVigenteSpecification;

    @Override
    public PaginatedResult<AsesorReadModel> consultarTodos(AsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, AsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                asesorQueryRepository.findAll(spec, pageable)
                        .map(AsesorQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<AsesorVigenteReadModel> consultarVigentes(AsesorVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, AsesorVigenteSortMapper::traducir);
        var spec = asesorVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                asesorVigenteQueryRepository.findAll(spec, pageable)
                        .map(AsesorQueryMapper::toReadModel));
    }
}

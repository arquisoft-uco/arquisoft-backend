package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.application.jurado.query.secondaryport.JuradoQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository.mapper.JuradoQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JuradoQueryOutputAdapter implements JuradoQueryOutputPort {

    private final JuradoVigenteQueryRepository juradoVigenteQueryRepository;
    private final JuradoVigenteJpaSpecification juradoVigenteSpecification;

    @Override
    public PaginatedResult<JuradoVigenteReadModel> consultarVigentes(JuradoVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, JuradoVigenteSortMapper::traducir);
        var spec = juradoVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                juradoVigenteQueryRepository.findAll(spec, pageable)
                        .map(JuradoQueryMapper::toReadModel));
    }
}

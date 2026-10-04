package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.application.bibliotecario.query.secondaryport.BibliotecarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository.mapper.BibliotecarioQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BibliotecarioQueryOutputAdapter implements BibliotecarioQueryOutputPort {

    private final BibliotecarioQueryRepository bibliotecarioQueryRepository;
    private final BibliotecarioJpaSpecification bibliotecarioSpecification;

    @Override
    public PaginatedResult<BibliotecarioReadModel> consultarTodos(BibliotecarioCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, BibliotecarioSortMapper::traducir);
        var spec = bibliotecarioSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                bibliotecarioQueryRepository.findAll(spec, pageable)
                        .map(BibliotecarioQueryMapper::toReadModel));
    }
}

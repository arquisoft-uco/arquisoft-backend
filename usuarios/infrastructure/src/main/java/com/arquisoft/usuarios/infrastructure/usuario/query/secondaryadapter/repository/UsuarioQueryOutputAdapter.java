package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper.UsuarioQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioQueryOutputAdapter implements UsuarioQueryOutputPort {

    private final UsuarioQueryRepository usuarioQueryRepository;
    private final UsuarioJpaSpecification usuarioSpecification;

    @Override
    public PaginatedResult<UsuarioReadModel> consultarTodos(UsuarioCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, UsuarioSortMapper::traducir);
        var spec = usuarioSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                usuarioQueryRepository.findAll(spec, pageable)
                        .map(UsuarioQueryMapper::toReadModel));
    }
}

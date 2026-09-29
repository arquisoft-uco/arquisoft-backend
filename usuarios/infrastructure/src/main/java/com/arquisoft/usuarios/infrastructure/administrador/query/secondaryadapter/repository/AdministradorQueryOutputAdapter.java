package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.application.administrador.query.secondaryport.AdministradorQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository.mapper.AdministradorQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdministradorQueryOutputAdapter implements AdministradorQueryOutputPort {

    private final AdministradorQueryRepository administradorQueryRepository;
    private final AdministradorJpaSpecification administradorSpecification;

    @Override
    public PaginatedResult<AdministradorReadModel> consultarTodos(AdministradorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, AdministradorSortMapper::traducir);
        var spec = administradorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                administradorQueryRepository.findAll(spec, pageable)
                        .map(AdministradorQueryMapper::toReadModel));
    }
}

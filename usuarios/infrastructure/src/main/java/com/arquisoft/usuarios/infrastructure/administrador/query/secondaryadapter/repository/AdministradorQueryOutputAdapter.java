package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
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

    private final AdministradorVigenteQueryRepository administradorVigenteQueryRepository;
    private final AdministradorVigenteJpaSpecification administradorVigenteSpecification;

    @Override
    public PaginatedResult<AdministradorVigenteReadModel> consultarVigentes(AdministradorVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, AdministradorVigenteSortMapper::traducir);
        var spec = administradorVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                administradorVigenteQueryRepository.findAll(spec, pageable)
                        .map(AdministradorQueryMapper::toReadModel));
    }
}

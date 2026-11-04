package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository.mapper.AsesorFichaQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorFichaQueryOutputAdapter implements AsesorFichaQueryOutputPort {

    private final AsesorFichaVigenteQueryRepository asesorFichaVigenteQueryRepository;
    private final AsesorFichaVigenteJpaSpecification asesorFichaVigenteSpecification;

    @Override
    public PaginatedResult<AsesorFichaVigenteReadModel> consultarVigentes(AsesorFichaVigenteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, AsesorFichaVigenteSortMapper::traducir);
        var spec = asesorFichaVigenteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                asesorFichaVigenteQueryRepository.findAll(spec, pageable)
                        .map(AsesorFichaQueryMapper::toReadModel));
    }
}

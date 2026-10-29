package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.secondaryport.EstadoAsesorQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository.mapper.EstadoAsesorQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoAsesorQueryOutputAdapter implements EstadoAsesorQueryOutputPort {

    private final EstadoAsesorQueryRepository repository;
    private final EstadoAsesorJpaSpecification specification;

    @Override
    public PaginatedResult<EstadoAsesorReadModel> consultarTodos(EstadoAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EstadoAsesorSortMapper::traducir, "id");
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                repository.findAll(spec, pageable).map(EstadoAsesorQueryMapper::toReadModel));
    }
}

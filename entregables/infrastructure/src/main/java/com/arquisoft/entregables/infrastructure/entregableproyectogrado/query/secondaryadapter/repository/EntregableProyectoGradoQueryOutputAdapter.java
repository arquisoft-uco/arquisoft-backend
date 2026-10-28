package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper.EntregableProyectoGradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoQueryOutputAdapter implements EntregableProyectoGradoQueryOutputPort {

    private static final String RUTA_DESEMPATE = "id";

    private final EntregableProyectoGradoQueryRepository entregableProyectoGradoQueryRepository;
    private final EntregableProyectoGradoJpaSpecification specification;

    @Override
    public PaginatedResult<EntregableProyectoGradoReadModel> consultar(EntregableProyectoGradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(
                criteria, EntregableProyectoGradoSortMapper::traducir, RUTA_DESEMPATE);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                entregableProyectoGradoQueryRepository.findAll(spec, pageable)
                        .map(EntregableProyectoGradoQueryMapper::toReadModel));
    }
}

package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.secondaryport.PlanRealQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper.PlanRealProyectoQueryMapper;
import com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository.mapper.PlanRealQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanRealQueryOutputAdapter implements PlanRealQueryOutputPort {

    private final PlanRealQueryRepository planRealQueryRepository;
    private final PlanRealJpaSpecification specification;
    private final PlanRealProyectoQueryRepository planRealProyectoQueryRepository;
    private final PlanRealProyectoJpaSpecification proyectoSpecification;

    @Override
    public PaginatedResult<PlanRealReadModel> consultarPorTarea(PlanRealTareaCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, PlanRealSortMapper::traducir);
        var spec = specification.deLaTarea(criteria.getTarea())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                planRealQueryRepository.findAll(spec, pageable)
                        .map(PlanRealQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<PlanRealProyectoReadModel> consultarPorProyectoGrado(PlanRealProyectoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, PlanRealProyectoSortMapper::traducir);
        var spec = proyectoSpecification.delProyectoGrado(criteria.getProyectoGrado())
                .and(proyectoSpecification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                planRealProyectoQueryRepository.findAll(spec, pageable)
                        .map(PlanRealProyectoQueryMapper::toReadModel));
    }
}

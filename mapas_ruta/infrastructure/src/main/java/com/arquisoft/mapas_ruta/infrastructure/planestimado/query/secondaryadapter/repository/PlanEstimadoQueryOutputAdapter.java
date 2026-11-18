package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.secondaryport.PlanEstimadoQueryOutputPort;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper.PlanEstimadoProyectoQueryMapper;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository.mapper.PlanEstimadoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanEstimadoQueryOutputAdapter implements PlanEstimadoQueryOutputPort {

    private final PlanEstimadoQueryRepository planEstimadoQueryRepository;
    private final PlanEstimadoJpaSpecification specification;
    private final PlanEstimadoProyectoQueryRepository planEstimadoProyectoQueryRepository;
    private final PlanEstimadoProyectoJpaSpecification proyectoSpecification;

    @Override
    public PaginatedResult<PlanEstimadoReadModel> consultarPorTarea(PlanEstimadoTareaCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, PlanEstimadoSortMapper::traducir);
        var spec = specification.deLaTarea(criteria.getTarea())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                planEstimadoQueryRepository.findAll(spec, pageable)
                        .map(PlanEstimadoQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<PlanEstimadoProyectoReadModel> consultarPorProyectoGrado(
            PlanEstimadoProyectoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, PlanEstimadoProyectoSortMapper::traducir);
        var spec = proyectoSpecification.delProyectoGrado(criteria.getProyectoGrado())
                .and(proyectoSpecification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                planEstimadoProyectoQueryRepository.findAll(spec, pageable)
                        .map(PlanEstimadoProyectoQueryMapper::toReadModel));
    }
}

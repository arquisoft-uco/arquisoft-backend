package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository.mapper.ProyectoGradoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProyectoGradoQueryOutputAdapter implements ProyectoGradoQueryOutputPort {

    private final ProyectoGradoQueryRepository proyectoGradoQueryRepository;
    private final ProyectoGradoJpaSpecification specification;
    private final ProyectoGradoEstudianteQueryRepository proyectoGradoEstudianteQueryRepository;
    private final ProyectoGradoAsesorQueryRepository proyectoGradoAsesorQueryRepository;
    private final ProyectoGradoAsesorJpaSpecification asesorSpecification;

    @Override
    public PaginatedResult<ProyectoGradoReadModel> consultarTodas(ProyectoGradoCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ProyectoGradoSortMapper::traducir);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                proyectoGradoQueryRepository.findAll(spec, pageable)
                        .map(ProyectoGradoQueryMapper::toReadModel));
    }

    @Override
    public List<ProyectoGradoReadModel> consultarPorEstudiante(ProyectoGradoEstudianteCriteria criteria) {
        return proyectoGradoEstudianteQueryRepository
                .findByEstudianteIdOrderByTituloProyectoAscProyectoGradoIdAsc(criteria.estudiante())
                .stream()
                .map(ProyectoGradoQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public PaginatedResult<ProyectoGradoReadModel> consultarPorAsesor(ProyectoGradoAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ProyectoGradoAsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                proyectoGradoAsesorQueryRepository.findAll(spec, pageable)
                        .map(ProyectoGradoQueryMapper::toReadModel));
    }
}

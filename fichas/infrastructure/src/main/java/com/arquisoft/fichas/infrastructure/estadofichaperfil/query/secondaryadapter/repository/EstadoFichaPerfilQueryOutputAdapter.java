package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper.EstadoFichaPerfilAsesorQueryMapper;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper.EstadoFichaPerfilQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilQueryOutputAdapter implements EstadoFichaPerfilQueryOutputPort {

    private final EstadoFichaPerfilEstudianteQueryRepository estadoFichaPerfilEstudianteQueryRepository;
    private final EstadoFichaPerfilAsesorQueryRepository estadoFichaPerfilAsesorQueryRepository;
    private final EstadoFichaPerfilAsesorJpaSpecification estadoFichaPerfilAsesorSpecification;

    @Override
    public List<EstadoFichaPerfilReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante) {
        return estadoFichaPerfilEstudianteQueryRepository
                .findByFichaPerfilIdAndEstudianteIdOrderByFechaActualizacionDesc(fichaPerfil, estudiante)
                .stream()
                .map(EstadoFichaPerfilQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public PaginatedResult<EstadoFichaPerfilAsesorReadModel> consultarPorAsesor(EstadoFichaPerfilAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EstadoFichaPerfilAsesorSortMapper::traducir);
        var spec = estadoFichaPerfilAsesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                estadoFichaPerfilAsesorQueryRepository.findAll(spec, pageable)
                        .map(EstadoFichaPerfilAsesorQueryMapper::toReadModel));
    }
}

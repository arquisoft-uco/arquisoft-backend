package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilAsesorQueryMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper.EvaluacionFichaPerfilQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionFichaPerfilQueryOutputAdapter implements EvaluacionFichaPerfilQueryOutputPort {

    private final EvaluacionFichaPerfilQueryRepository evaluacionFichaPerfilQueryRepository;
    private final EvaluacionFichaPerfilEstudianteQueryRepository evaluacionFichaPerfilEstudianteQueryRepository;
    private final EvaluacionFichaPerfilAsesorQueryRepository evaluacionFichaPerfilAsesorQueryRepository;
    private final EvaluacionFichaPerfilAsesorJpaSpecification evaluacionFichaPerfilAsesorSpecification;

    @Override
    public List<EvaluacionFichaPerfilReadModel> consultarPorFichaYRepresentante(
            UUID fichaPerfil, UUID representanteComite) {
        return evaluacionFichaPerfilQueryRepository
                .findByFichaPerfilIdAndRepresentanteComiteIdOrderByFechaCreacionAsc(fichaPerfil, representanteComite)
                .stream()
                .map(EvaluacionFichaPerfilQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<EvaluacionFichaPerfilEstudianteReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante) {
        return evaluacionFichaPerfilEstudianteQueryRepository
                .findByFichaPerfilIdAndEstudianteIdOrderByFechaCreacionAsc(fichaPerfil, estudiante)
                .stream()
                .map(EvaluacionFichaPerfilEstudianteQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public PaginatedResult<EvaluacionFichaPerfilAsesorReadModel> consultarPorAsesor(EvaluacionFichaPerfilAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, EvaluacionFichaPerfilAsesorSortMapper::traducir);
        var spec = evaluacionFichaPerfilAsesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                evaluacionFichaPerfilAsesorQueryRepository.findAll(spec, pageable)
                        .map(EvaluacionFichaPerfilAsesorQueryMapper::toReadModel));
    }
}

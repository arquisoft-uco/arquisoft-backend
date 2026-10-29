package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoReadModel;
import com.arquisoft.artefactos.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper.ArtefactoAsesorQueryMapper;
import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper.ArtefactoEstudianteQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtefactoQueryOutputAdapter implements ArtefactoQueryOutputPort {

    private final ArtefactoAsesorQueryRepository artefactoAsesorRepository;
    private final ArtefactoAsesorJpaSpecification asesorSpecification;
    private final ArtefactoEstudianteQueryRepository artefactoEstudianteRepository;
    private final ArtefactoEstudianteJpaSpecification estudianteSpecification;

    @Override
    public PaginatedResult<ArtefactoReadModel> consultarTodosAsesor(ArtefactoAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ArtefactoAsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                artefactoAsesorRepository.findAll(spec, pageable)
                        .map(ArtefactoAsesorQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<ArtefactoEstudianteReadModel> consultarTodosEstudiante(
            ArtefactoEstudianteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, ArtefactoEstudianteSortMapper::traducir);
        var spec = estudianteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                artefactoEstudianteRepository.findAll(spec, pageable)
                        .map(ArtefactoEstudianteQueryMapper::toReadModel));
    }
}

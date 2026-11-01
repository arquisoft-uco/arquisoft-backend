package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.application.revisionasesor.query.secondaryport.RevisionAsesorQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper.RevisionAsesorAsesorQueryMapper;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper.RevisionAsesorEstudianteQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RevisionAsesorQueryOutputAdapter implements RevisionAsesorQueryOutputPort {

    private final RevisionAsesorEstudianteQueryRepository revisionAsesorEstudianteRepository;
    private final RevisionAsesorEstudianteJpaSpecification estudianteSpecification;
    private final RevisionAsesorAsesorQueryRepository revisionAsesorAsesorRepository;
    private final RevisionAsesorAsesorJpaSpecification asesorSpecification;

    @Override
    public PaginatedResult<RevisionAsesorReadModel> consultarTodasEstudiante(RevisionAsesorEstudianteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RevisionAsesorEstudianteSortMapper::traducir);
        var spec = estudianteSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                revisionAsesorEstudianteRepository.findAll(spec, pageable)
                        .map(RevisionAsesorEstudianteQueryMapper::toReadModel));
    }

    @Override
    public PaginatedResult<RevisionAsesorAsesorReadModel> consultarTodasAsesor(RevisionAsesorAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, RevisionAsesorAsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                revisionAsesorAsesorRepository.findAll(spec, pageable)
                        .map(RevisionAsesorAsesorQueryMapper::toReadModel));
    }
}

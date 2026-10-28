package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.mapper.VersionArtefactoAsesorQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VersionArtefactoQueryOutputAdapter implements VersionArtefactoQueryOutputPort {

    private final VersionArtefactoAsesorQueryRepository versionArtefactoAsesorRepository;
    private final VersionArtefactoAsesorJpaSpecification asesorSpecification;

    @Override
    public PaginatedResult<VersionArtefactoReadModel> consultarTodasAsesor(VersionArtefactoAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, VersionArtefactoAsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                versionArtefactoAsesorRepository.findAll(spec, pageable)
                        .map(VersionArtefactoAsesorQueryMapper::toReadModel));
    }
}

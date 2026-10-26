package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper.VersionRepositorioArtefactoAsesorQueryMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper.VersionRepositorioArtefactoQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoQueryOutputAdapter implements VersionRepositorioArtefactoQueryOutputPort {

    private final VersionRepositorioArtefactoQueryRepository versionRepositorioArtefactoQueryRepository;
    private final VersionRepositorioArtefactoAsesorQueryRepository versionRepositorioArtefactoAsesorQueryRepository;
    private final VersionRepositorioArtefactoAsesorJpaSpecification asesorSpecification;

    @Override
    public List<VersionRepositorioArtefactoReadModel> consultarPorRepositorio(UUID repositorioArtefacto) {
        return versionRepositorioArtefactoQueryRepository
                .findByRepositorioArtefactoOrderByNumeroVersionDescNombreDocumentoAsc(repositorioArtefacto)
                .stream()
                .map(VersionRepositorioArtefactoQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public PaginatedResult<VersionRepositorioArtefactoAsesorReadModel> consultarParaAsesor(
            VersionRepositorioArtefactoAsesorCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, VersionRepositorioArtefactoAsesorSortMapper::traducir);
        var spec = asesorSpecification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                versionRepositorioArtefactoAsesorQueryRepository.findAll(spec, pageable)
                        .map(VersionRepositorioArtefactoAsesorQueryMapper::toReadModel));
    }
}

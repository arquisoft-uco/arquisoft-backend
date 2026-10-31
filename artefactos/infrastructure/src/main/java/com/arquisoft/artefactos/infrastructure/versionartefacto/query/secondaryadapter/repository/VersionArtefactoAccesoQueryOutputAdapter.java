package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoAccesoQueryOutputAdapter implements VersionArtefactoAccesoQueryOutputPort {

    private final VersionArtefactoAsesorQueryRepository versionArtefactoAsesorQueryRepository;
    private final VersionArtefactoEstudianteQueryRepository versionArtefactoEstudianteQueryRepository;

    @Override
    public Optional<UUID> obtenerAsesorPorId(UUID versionArtefacto) {
        return versionArtefactoAsesorQueryRepository.findById(versionArtefacto)
                .map(VersionArtefactoAsesorJpaQueryEntity::getAsesorId);
    }

    @Override
    public boolean existeParticipacion(UUID versionArtefacto, UUID estudiante) {
        return versionArtefactoEstudianteQueryRepository.existsByIdAndEstudianteId(versionArtefacto, estudiante);
    }
}

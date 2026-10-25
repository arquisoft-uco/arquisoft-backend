package com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.mapper.AsesorJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorCommandOutputAdapter implements AsesorOutputPort {

    private final AsesorCommandRepository asesorCommandRepository;

    @Override
    public Optional<AsesorEntity> obtenerPorRevisionAsesor(UUID revisionAsesor) {
        return asesorCommandRepository.findByRevisionAsesor(revisionAsesor).map(AsesorJpaMapper::toEntity);
    }
}

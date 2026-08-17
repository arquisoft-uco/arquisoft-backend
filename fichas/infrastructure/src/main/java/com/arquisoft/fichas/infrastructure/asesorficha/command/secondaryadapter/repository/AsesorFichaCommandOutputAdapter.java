package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaCommandRepository asesorFichaCommandRepository;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaCommandRepository.existsById(id);
    }

    @Override
    public Optional<AsesorFichaEntity> buscarContactoPorId(UUID id) {
        return asesorFichaCommandRepository.findById(id).map(AsesorFichaJpaMapper::toEntity);
    }
}

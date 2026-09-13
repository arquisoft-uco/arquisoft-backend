package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaFichasCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaFichasCommandRepository asesorFichaCommandRepository;
    private final AppLogger logger;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaCommandRepository.existsById(id);
    }

    @Override
    public Optional<AsesorFichaEntity> buscarContactoPorId(UUID id) {
        return asesorFichaCommandRepository.findById(id).map(AsesorFichaJpaMapper::toEntity);
    }

    @Override
    public void guardar(AsesorFichaEntity asesorFicha) {
        asesorFichaCommandRepository.save(AsesorFichaJpaMapper.toJpaEntity(asesorFicha));
        logger.debug(AsesorFichaKey.LOG_GUARDADO, asesorFicha.id());
    }

    @Override
    public Optional<AsesorFichaEntity> obtenerPorId(UUID id) {
        return buscarContactoPorId(id);
    }
}

package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaCommandRepository asesorFichaCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<AsesorFichaEntity> obtenerVigentePorId(UUID id) {
        return asesorFichaCommandRepository.findByIdAndEliminadoEnIsNull(id).map(AsesorFichaJpaMapper::toEntity);
    }

    @Override
    public void guardar(AsesorFichaEntity asesorFicha) {
        asesorFichaCommandRepository.save(AsesorFichaJpaMapper.toJpaEntity(asesorFicha));
        logger.debug(AsesorFichaKey.LOG_GUARDADO, asesorFicha.id());
    }

    @Override
    public Optional<AsesorFichaEntity> obtenerPorId(UUID id) {
        return asesorFichaCommandRepository.findById(id).map(AsesorFichaJpaMapper::toEntity);
    }

    @Override
    public void actualizar(AsesorFichaEntity asesorFicha) {
        asesorFichaCommandRepository.save(AsesorFichaJpaMapper.toJpaEntity(asesorFicha));
        logger.debug(AsesorFichaKey.LOG_ACTUALIZADO, asesorFicha.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        asesorFichaCommandRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(AsesorFichaKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public void reactivar(AsesorFichaEntity asesorFicha) {
        asesorFichaCommandRepository.reactivar(asesorFicha.id(), asesorFicha.identificador(), asesorFicha.nombre(),
                asesorFicha.email(), asesorFicha.ocurridoEn());
        logger.debug(AsesorFichaKey.LOG_ACTUALIZADO, asesorFicha.id());
    }
}

package com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.asesor.command.secondaryadapter.mapper.AsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorCommandOutputAdapter implements AsesorOutputPort {

    private final AsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void guardar(AsesorEntity asesor) {
        repository.save(AsesorJpaMapper.toJpaEntity(asesor));
        logger.debug(AsesorKey.LOG_GUARDADO, asesor.id());
    }

    @Override
    public void reactivar(AsesorEntity asesor) {
        repository.reactivar(asesor.id(), asesor.identificador(), asesor.nombre(), asesor.email(),
                asesor.ocurridoEn());
        logger.debug(AsesorKey.LOG_ACTUALIZADO, asesor.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        repository.eliminarLogica(id, ocurridoEn);
        logger.debug(AsesorKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public Optional<AsesorEntity> obtenerPorId(UUID id) {
        return repository.findById(id).map(AsesorJpaMapper::toEntity);
    }

    @Override
    public boolean existeVigentePorId(UUID id) {
        return repository.existsByIdAndEliminadoEnIsNull(id);
    }

    @Override
    public Optional<AsesorEntity> obtenerContactoPorEvaluacion(UUID evaluacion) {
        return repository.findContactoPorEvaluacion(evaluacion).map(AsesorJpaMapper::toEntity);
    }
}

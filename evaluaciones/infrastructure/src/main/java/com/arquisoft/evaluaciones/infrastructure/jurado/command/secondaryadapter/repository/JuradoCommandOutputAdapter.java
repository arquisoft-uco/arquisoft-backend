package com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.jurado.command.secondaryadapter.mapper.JuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoCommandOutputAdapter implements JuradoOutputPort {

    private final JuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Set<UUID> consultarVigentesPorIds(Set<UUID> jurados) {
        return repository.findIdsVigentes(jurados);
    }

    @Override
    public void guardar(JuradoEntity jurado) {
        repository.save(JuradoJpaMapper.toJpaEntity(jurado));
        logger.debug(JuradoKey.LOG_GUARDADO, jurado.id());
    }

    @Override
    public void reactivar(JuradoEntity jurado) {
        repository.reactivar(jurado.id(), jurado.identificador(), jurado.nombre(), jurado.email(),
                jurado.ocurridoEn());
        logger.debug(JuradoKey.LOG_ACTUALIZADO, jurado.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        repository.eliminarLogica(id, ocurridoEn);
        logger.debug(JuradoKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public Optional<JuradoEntity> obtenerPorId(UUID id) {
        return repository.findById(id).map(JuradoJpaMapper::toEntity);
    }
}

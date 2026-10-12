package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteRepository;
    private final AppLogger logger;

    @Override
    public boolean existePorId(UUID id) {
        return estudianteRepository.existsById(id);
    }

    @Override
    public List<EstudianteEntity> buscarPorIds(List<UUID> ids) {
        return estudianteRepository.findAllById(ids).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }

    @Override
    public List<UUID> buscarIdsVigentes(List<UUID> ids) {
        return estudianteRepository.findIdsVigentesByIdIn(ids);
    }

    @Override
    public void guardar(EstudianteEntity estudiante) {
        estudianteRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteKey.LOG_GUARDADO, estudiante.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        estudianteRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public void reactivar(EstudianteEntity estudiante) {
        estudianteRepository.reactivar(estudiante.id(), estudiante.identificador(), estudiante.nombre(),
                estudiante.email(), estudiante.ocurridoEn());
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, estudiante.id());
    }

    @Override
    public Optional<EstudianteEntity> obtenerPorId(UUID id) {
        return estudianteRepository.findById(id).map(EstudianteJpaMapper::toEntity);
    }

    @Override
    public void actualizar(EstudianteEntity estudiante) {
        estudianteRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, estudiante.id());
    }
}

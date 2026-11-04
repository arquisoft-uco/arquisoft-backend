package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteCommandRepository;
    private final AppLogger logger;

    @Override
    public List<EstudianteEntity> obtenerVigentesVinculadosAlArtefacto(UUID artefacto) {
        return estudianteCommandRepository.buscarVigentesVinculadosAlArtefacto(artefacto).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }

    @Override
    public List<EstudianteEntity> obtenerPorIds(List<UUID> ids) {
        return estudianteCommandRepository.findAllById(ids).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }

    @Override
    public void guardar(EstudianteEntity estudiante) {
        estudianteCommandRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteKey.LOG_GUARDADO, estudiante.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        estudianteCommandRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public void reactivar(EstudianteEntity estudiante) {
        estudianteCommandRepository.reactivar(estudiante.id(), estudiante.identificador(), estudiante.nombre(),
                estudiante.email(), estudiante.ocurridoEn());
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, estudiante.id());
    }

    @Override
    public Optional<EstudianteEntity> obtenerPorId(UUID id) {
        return estudianteCommandRepository.findById(id).map(EstudianteJpaMapper::toEntity);
    }

    @Override
    public void actualizar(EstudianteEntity estudiante) {
        estudianteCommandRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteKey.LOG_ACTUALIZADO, estudiante.id());
    }
}

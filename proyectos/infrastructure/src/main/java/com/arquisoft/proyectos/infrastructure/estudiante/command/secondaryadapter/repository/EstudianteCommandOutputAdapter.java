package com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
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
    public void guardar(EstudianteEntity estudiante) {
        estudianteRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteProyectosKey.LOG_GUARDADO, estudiante.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        estudianteRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(EstudianteProyectosKey.LOG_FILA_ELIMINADA, id);
    }

    @Override
    public void reactivar(EstudianteEntity estudiante) {
        estudianteRepository.reactivar(estudiante.id(), estudiante.identificador(), estudiante.nombre(),
                estudiante.email(), estudiante.ocurridoEn());
        logger.debug(EstudianteProyectosKey.LOG_FILA_REACTIVADA, estudiante.id());
    }

    @Override
    public Optional<EstudianteEntity> obtenerPorId(UUID id) {
        return estudianteRepository.findById(id).map(EstudianteJpaMapper::toEntity);
    }

    @Override
    public void actualizar(EstudianteEntity estudiante) {
        estudianteRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(EstudianteProyectosKey.LOG_FILA_ACTUALIZADA, estudiante.id());
    }

    @Override
    public List<EstudianteEntity> obtenerVigentesPorIds(List<UUID> ids) {
        return estudianteRepository.findAllByIdInAndEliminadoEnIsNull(ids).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }

    @Override
    public List<EstudianteEntity> obtenerVigentesPorProyectoGrado(UUID proyectoGrado) {
        return estudianteRepository.buscarVigentesPorProyectoGrado(proyectoGrado).stream()
                .map(EstudianteJpaMapper::toEntity)
                .toList();
    }
}

package com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCommandOutputAdapter implements ProyectoGradoOutputPort {

    private final ProyectoGradoCommandRepository proyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(ProyectoGradoEntity proyecto) {
        proyectoGradoCommandRepository.save(ProyectoGradoJpaMapper.toJpaEntity(proyecto));
        logger.debug(ProyectoGradoKey.LOG_GUARDADO, proyecto.id());
    }

    @Override
    public boolean existePorId(UUID id) {
        return proyectoGradoCommandRepository.existsById(id);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorId(UUID id) {
        return proyectoGradoCommandRepository.findById(id).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorId(UUID id) {
        return proyectoGradoCommandRepository.buscarBloqueadoPorId(id).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorEntregable(UUID entregable) {
        return proyectoGradoCommandRepository.buscarBloqueadoPorEntregable(entregable).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstado(UUID proyectoGrado, String estadoProyecto, Instant ocurridoEn) {
        proyectoGradoCommandRepository.actualizarEstado(proyectoGrado, estadoProyecto, ocurridoEn);
        logger.debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, proyectoGrado, estadoProyecto, ocurridoEn);
    }
}

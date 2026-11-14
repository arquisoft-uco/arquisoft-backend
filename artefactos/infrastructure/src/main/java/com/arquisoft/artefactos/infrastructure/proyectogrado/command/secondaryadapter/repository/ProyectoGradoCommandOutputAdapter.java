package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
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
        return proyectoGradoCommandRepository.bloquearPorId(id).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorArtefacto(UUID artefacto) {
        return proyectoGradoCommandRepository.obtenerPorArtefacto(artefacto).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorArtefacto(UUID artefacto) {
        return proyectoGradoCommandRepository.bloquearPorArtefacto(artefacto).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorRevisionAsesor(UUID revisionAsesor) {
        return proyectoGradoCommandRepository.bloquearPorRevisionAsesor(revisionAsesor)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorObservacion(UUID observacion) {
        return proyectoGradoCommandRepository.bloquearPorObservacion(observacion)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorVersionArtefacto(UUID versionArtefacto) {
        return proyectoGradoCommandRepository.bloquearPorVersionArtefacto(versionArtefacto)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void marcarEntregado(UUID id, Instant entregadoEn) {
        proyectoGradoCommandRepository.marcarEntregado(id, entregadoEn);
        logger.debug(ProyectoGradoKey.LOG_ENTREGADO_MARCADO, id, entregadoEn);
    }

    @Override
    public void limpiarEntregado(UUID id, Instant hasta) {
        proyectoGradoCommandRepository.limpiarEntregado(id, hasta);
        logger.debug(ProyectoGradoKey.LOG_ENTREGADO_LIMPIADO, id, hasta);
    }

    @Override
    public void actualizarEstado(UUID id, String estadoProyecto, Instant ocurridoEn) {
        proyectoGradoCommandRepository.actualizarEstado(id, estadoProyecto, ocurridoEn);
        logger.debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, id, estadoProyecto, ocurridoEn);
    }
}

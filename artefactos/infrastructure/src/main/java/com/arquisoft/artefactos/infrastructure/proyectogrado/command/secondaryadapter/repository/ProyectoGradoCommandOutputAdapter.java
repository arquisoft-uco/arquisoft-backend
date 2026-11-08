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
    public Optional<ProyectoGradoEntity> obtenerPorArtefacto(UUID artefacto) {
        return proyectoGradoCommandRepository.obtenerPorArtefacto(artefacto).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerBloqueadoPorArtefacto(UUID artefacto) {
        return proyectoGradoCommandRepository.bloquearPorArtefacto(artefacto).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorRevisionAsesor(UUID revisionAsesor) {
        return proyectoGradoCommandRepository.obtenerPorRevisionAsesor(revisionAsesor)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorObservacion(UUID observacion) {
        return proyectoGradoCommandRepository.obtenerPorObservacion(observacion)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorVersionArtefacto(UUID versionArtefacto) {
        return proyectoGradoCommandRepository.obtenerPorVersionArtefacto(versionArtefacto)
                .map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void marcarEntregado(UUID id, Instant entregadoEn) {
        proyectoGradoCommandRepository.marcarEntregado(id, entregadoEn);
        logger.debug(ProyectoGradoKey.LOG_ENTREGADO_MARCADO, id, entregadoEn);
    }

    @Override
    public void marcarFinalizado(UUID id, Instant finalizadoEn) {
        proyectoGradoCommandRepository.marcarFinalizado(id, finalizadoEn);
        logger.debug(ProyectoGradoKey.LOG_FINALIZADO_MARCADO, id, finalizadoEn);
    }
}

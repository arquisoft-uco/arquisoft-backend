package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.mapper.ArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoCommandOutputAdapter implements ArtefactoOutputPort {

    private final ArtefactoCommandRepository artefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(ArtefactoEntity artefacto) {
        artefactoCommandRepository.save(ArtefactoJpaMapper.toJpaEntity(artefacto));
        logger.debug(ArtefactoKey.LOG_GUARDADO, artefacto.id());
    }

    @Override
    public void traspasarAsesorDeProyecto(UUID proyectoGrado, UUID asesor) {
        var traspasados = artefactoCommandRepository.traspasarAsesorDeProyecto(proyectoGrado, asesor);
        logger.debug(ArtefactoKey.LOG_TRASPASADOS, traspasados);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorId(UUID id) {
        return artefactoCommandRepository.findById(id).map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerBloqueadoPorId(UUID id) {
        return artefactoCommandRepository.bloquearPorId(id).map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorVersionArtefacto(UUID versionArtefacto) {
        return artefactoCommandRepository.buscarPorVersionArtefacto(versionArtefacto)
                .map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public void cambiarAsesor(UUID artefacto, UUID asesor) {
        artefactoCommandRepository.cambiarAsesor(artefacto, asesor);
        logger.debug(ArtefactoKey.LOG_ASESOR_ACTUALIZADO, artefacto);
    }

    @Override
    public void omitir(UUID artefacto, Instant omitidoEn) {
        artefactoCommandRepository.omitir(artefacto, omitidoEn);
        logger.debug(ArtefactoKey.LOG_OMITIDO_ACTUALIZADO, artefacto);
    }

    @Override
    public void reactivar(UUID artefacto, UUID asesor) {
        artefactoCommandRepository.reactivar(artefacto, asesor);
        logger.debug(ArtefactoKey.LOG_REACTIVADO_ACTUALIZADO, artefacto);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerOmitidoPorProyectoYRepositorio(
            UUID proyectoGrado, UUID versionRepositorioArtefacto) {
        return artefactoCommandRepository
                .buscarOmitidoPorProyectoYRepositorio(proyectoGrado, versionRepositorioArtefacto)
                .map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorRevisionAsesor(UUID revisionAsesor) {
        return artefactoCommandRepository.buscarPorRevisionAsesor(revisionAsesor)
                .map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorObservacion(UUID observacion) {
        return artefactoCommandRepository.buscarPorObservacion(observacion)
                .map(ArtefactoJpaMapper::toEntity);
    }
}

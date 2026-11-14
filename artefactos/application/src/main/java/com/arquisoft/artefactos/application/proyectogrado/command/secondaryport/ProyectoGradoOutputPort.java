package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorArtefacto(UUID artefacto);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorArtefacto(UUID artefacto);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorRevisionAsesor(UUID revisionAsesor);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorObservacion(UUID observacion);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorVersionArtefacto(UUID versionArtefacto);

    void marcarEntregado(UUID id, Instant entregadoEn);

    void limpiarEntregado(UUID id, Instant hasta);

    void marcarFinalizado(UUID id, Instant finalizadoEn);

    void marcarCancelado(UUID id, Instant canceladoEn);
}

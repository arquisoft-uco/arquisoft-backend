package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void guardar(ProyectoGradoEntity proyecto);

    boolean existePorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorId(UUID id);

    Optional<ProyectoGradoEntity> obtenerPorArtefacto(UUID artefacto);

    Optional<ProyectoGradoEntity> obtenerBloqueadoPorArtefacto(UUID artefacto);

    Optional<ProyectoGradoEntity> obtenerPorRevisionAsesor(UUID revisionAsesor);

    Optional<ProyectoGradoEntity> obtenerPorObservacion(UUID observacion);

    Optional<ProyectoGradoEntity> obtenerPorVersionArtefacto(UUID versionArtefacto);

    void marcarEntregado(UUID id, Instant entregadoEn);

    void limpiarEntregado(UUID id, Instant hasta);

    void marcarFinalizado(UUID id, Instant finalizadoEn);

    void marcarCancelado(UUID id, Instant canceladoEn);
}

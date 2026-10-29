package com.arquisoft.artefactos.application.artefacto.command.secondaryport;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArtefactoOutputPort {

    void registrar(ArtefactoEntity artefacto);

    void traspasarAsesorDeProyecto(UUID proyectoGrado, UUID asesor);

    Optional<ArtefactoEntity> obtenerPorId(UUID id);

    Optional<ArtefactoEntity> obtenerBloqueadoPorId(UUID id);

    Optional<ArtefactoEntity> obtenerPorVersionArtefacto(UUID versionArtefacto);

    void cambiarAsesor(UUID artefacto, UUID asesor);
}

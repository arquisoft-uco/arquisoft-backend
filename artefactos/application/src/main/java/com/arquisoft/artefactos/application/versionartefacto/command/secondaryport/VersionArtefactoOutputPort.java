package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VersionArtefactoOutputPort {

    void registrar(VersionArtefactoEntity version);

    int ultimoNumeroPorArtefacto(UUID artefacto);

    Optional<VersionArtefactoEntity> obtenerPorId(UUID id);

    boolean esUltimaDeSuArtefacto(UUID versionArtefacto);

    Optional<VersionArtefactoEntity> obtenerBloqueadaPorId(UUID id);

    boolean existeParticipacion(UUID versionArtefacto, UUID estudiante);

    void eliminar(UUID versionArtefacto);

    long tamanoVigenteDelProyectoDeArtefacto(UUID artefacto, String estadoCerrada);

    long tamanoVigenteDelArtefacto(UUID artefacto, String estadoCerrada);

    List<String> obtenerUrisArchivoPorProyectoGrado(UUID proyectoGrado);
}

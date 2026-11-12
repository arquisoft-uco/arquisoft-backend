package com.arquisoft.entregables.application.artefacto.command.secondaryport;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtefactoOutputPort {

    void registrarVersionCerrada(ArtefactoEntity artefacto);

    void marcarOmision(UUID id, Instant omitidoEn, Instant ocurridoEn);

    void limpiarOmision(UUID id, Instant ocurridoEn);

    Optional<ArtefactoEntity> obtenerPorId(UUID id);

    List<ArtefactoEntity> obtenerVigentesPorProyecto(UUID proyectoGrado);

    List<String> obtenerClavesPorProyecto(UUID proyectoGrado);
}

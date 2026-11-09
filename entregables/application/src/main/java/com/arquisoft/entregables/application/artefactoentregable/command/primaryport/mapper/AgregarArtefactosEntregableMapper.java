package com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.ArtefactoPaqueteCommand;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;

import java.util.List;
import java.util.UUID;

public final class AgregarArtefactosEntregableMapper {

    private AgregarArtefactosEntregableMapper() {}

    public static AgregacionArtefactosEntregableDomain toDomain(
            UUID entregableProyectoGrado, List<ArtefactoPaqueteCommand> artefactos) {
        var snapshots = artefactos.stream()
                .map(artefacto -> ArtefactoEntregableDomain.crear(
                        entregableProyectoGrado,
                        artefacto.rutaEnPaquete(),
                        artefacto.artefacto(),
                        artefacto.versionRepositorioArtefacto(),
                        artefacto.nombreDocumento(),
                        artefacto.repositorio(),
                        artefacto.nombreRepositorio()))
                .toList();
        return AgregacionArtefactosEntregableDomain.crear(entregableProyectoGrado, snapshots);
    }
}

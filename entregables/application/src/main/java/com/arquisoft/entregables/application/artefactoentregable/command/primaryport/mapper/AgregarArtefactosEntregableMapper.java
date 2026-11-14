package com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain.DatosRepositorioArtefacto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AgregarArtefactosEntregableMapper {

    private AgregarArtefactosEntregableMapper() {}

    public static AgregacionArtefactosEntregableDomain toDomain(
            UUID entregableProyectoGrado, List<ArtefactoDomain> artefactos, List<EntradaPaqueteArmada> entradas) {
        var rutaPorArtefacto = entradas.stream()
                .collect(Collectors.toMap(EntradaPaqueteArmada::artefacto, EntradaPaqueteArmada::rutaEnPaquete));
        var snapshots = artefactos.stream()
                .map(artefacto -> ArtefactoEntregableDomain.crear(
                        entregableProyectoGrado,
                        rutaPorArtefacto.get(artefacto.getId()),
                        artefacto.getId(),
                        artefacto.getVersionArtefacto(),
                        artefacto.getNumeroVersion(),
                        artefacto.getNombreDocumento(),
                        new DatosRepositorioArtefacto(
                                artefacto.getVersionRepositorioArtefacto(),
                                artefacto.getRepositorio(),
                                artefacto.getNombreRepositorio())))
                .toList();
        return AgregacionArtefactosEntregableDomain.crear(entregableProyectoGrado, snapshots);
    }
}

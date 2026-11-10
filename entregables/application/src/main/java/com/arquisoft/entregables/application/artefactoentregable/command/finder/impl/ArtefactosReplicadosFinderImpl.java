package com.arquisoft.entregables.application.artefactoentregable.command.finder.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.finder.ArtefactosReplicadosFinder;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactosReplicadosFinderImpl implements ArtefactosReplicadosFinder {

    private final ArtefactoEntregableOutputPort artefactoEntregableOutputPort;

    @Override
    public List<UUID> obtener(List<UUID> versionesArtefacto) {
        if (UtilColeccion.esVaciaONula(versionesArtefacto)) {
            return List.of();
        }
        return artefactoEntregableOutputPort.obtenerIdsExistentes(versionesArtefacto);
    }
}

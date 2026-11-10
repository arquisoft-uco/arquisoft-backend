package com.arquisoft.entregables.application.artefactoentregable.command.secondaryport;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;

import java.util.List;
import java.util.UUID;

public interface ArtefactoEntregableOutputPort {

    List<UUID> obtenerIdsExistentes(List<UUID> versionesArtefacto);

    void guardarArtefactos(List<ArtefactoEntregableEntity> artefactos);

    void vincular(List<ArtefactoEntregableEntity> artefactos);
}

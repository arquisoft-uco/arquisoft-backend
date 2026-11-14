package com.arquisoft.entregables.application.artefactoentregable.command.secondaryport;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;

import java.util.List;

public interface ArtefactoEntregableOutputPort {

    void vincular(List<ArtefactoEntregableEntity> artefactos);
}

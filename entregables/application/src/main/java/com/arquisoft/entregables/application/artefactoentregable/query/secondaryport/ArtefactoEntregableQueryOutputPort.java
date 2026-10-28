package com.arquisoft.entregables.application.artefactoentregable.query.secondaryport;

import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;

import java.util.List;
import java.util.UUID;

public interface ArtefactoEntregableQueryOutputPort {

    List<ArtefactoEntregableReadModel> consultarPorEntregable(UUID entregableProyectoGrado);
}

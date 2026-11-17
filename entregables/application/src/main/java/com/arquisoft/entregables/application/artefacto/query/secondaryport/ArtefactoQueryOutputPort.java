package com.arquisoft.entregables.application.artefacto.query.secondaryport;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;

import java.util.List;
import java.util.UUID;

public interface ArtefactoQueryOutputPort {

    List<ArtefactoVistaPreviaReadModel> consultarVigentesPorProyecto(UUID proyectoGrado);
}

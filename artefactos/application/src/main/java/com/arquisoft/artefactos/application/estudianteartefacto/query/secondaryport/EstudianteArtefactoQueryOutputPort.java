package com.arquisoft.artefactos.application.estudianteartefacto.query.secondaryport;

import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoQueryOutputPort {

    List<EstudianteArtefactoReadModel> consultarPorArtefacto(UUID artefacto);
}

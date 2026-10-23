package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.EstudianteArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarEstudiantesArtefactoInteractor
        extends Interactor<ConsultarEstudiantesArtefactoQuery, List<EstudianteArtefactoReadModel>> {}

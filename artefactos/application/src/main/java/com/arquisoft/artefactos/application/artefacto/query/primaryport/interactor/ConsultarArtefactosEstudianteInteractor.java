package com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosEstudianteQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarArtefactosEstudianteInteractor
        extends Interactor<ConsultarArtefactosEstudianteQuery, PaginatedResult<ArtefactoEstudianteReadModel>> {}

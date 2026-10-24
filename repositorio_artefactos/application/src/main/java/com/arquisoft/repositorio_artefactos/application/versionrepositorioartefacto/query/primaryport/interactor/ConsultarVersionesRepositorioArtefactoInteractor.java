package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarVersionesRepositorioArtefactoInteractor
        extends Interactor<ConsultarVersionesRepositorioArtefactoQuery, List<VersionRepositorioArtefactoReadModel>> {}

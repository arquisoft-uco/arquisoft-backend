package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface VersionArtefactoQueryOutputPort {

    PaginatedResult<VersionArtefactoReadModel> consultarTodasAsesor(VersionArtefactoAsesorCriteria criteria);
}

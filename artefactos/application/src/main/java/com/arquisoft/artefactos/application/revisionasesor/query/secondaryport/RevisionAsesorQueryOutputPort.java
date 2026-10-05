package com.arquisoft.artefactos.application.revisionasesor.query.secondaryport;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface RevisionAsesorQueryOutputPort {

    PaginatedResult<RevisionAsesorReadModel> consultarTodasEstudiante(RevisionAsesorEstudianteCriteria criteria);
}

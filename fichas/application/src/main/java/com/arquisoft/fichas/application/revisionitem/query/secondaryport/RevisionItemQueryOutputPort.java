package com.arquisoft.fichas.application.revisionitem.query.secondaryport;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface RevisionItemQueryOutputPort {

    PaginatedResult<RevisionItemReadModel> consultarTodas(RevisionItemCriteria criteria);

    PaginatedResult<RevisionItemReadModel> consultarTodasEstudiante(RevisionItemEstudianteCriteria criteria);
}

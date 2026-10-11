package com.arquisoft.fichas.application.revisionitem.query.usecase;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRevisionesItemEstudianteUseCase
        extends UseCase<RevisionItemEstudianteCriteria, PaginatedResult<RevisionItemReadModel>> {}

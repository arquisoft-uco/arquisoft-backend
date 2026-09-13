package com.arquisoft.fichas.application.revisionitem.query.usecase;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarRevisionesItemAsesorUseCase
        extends UseCase<RevisionItemCriteria, PaginatedResult<RevisionItemReadModel>> {}

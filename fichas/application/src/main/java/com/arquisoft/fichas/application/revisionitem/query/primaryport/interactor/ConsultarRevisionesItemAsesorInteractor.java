package com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemAsesorQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRevisionesItemAsesorInteractor
        extends Interactor<ConsultarRevisionesItemAsesorQuery, PaginatedResult<RevisionItemReadModel>> {}

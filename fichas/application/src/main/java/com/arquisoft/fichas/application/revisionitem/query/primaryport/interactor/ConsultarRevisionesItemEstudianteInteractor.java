package com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarRevisionesItemEstudianteInteractor
        extends Interactor<ConsultarRevisionesItemEstudianteQuery, PaginatedResult<RevisionItemReadModel>> {}

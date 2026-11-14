package com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarDocumentacionesInteractor
        extends Interactor<ConsultarDocumentacionesQuery, PaginatedResult<DocumentacionReadModel>> {}

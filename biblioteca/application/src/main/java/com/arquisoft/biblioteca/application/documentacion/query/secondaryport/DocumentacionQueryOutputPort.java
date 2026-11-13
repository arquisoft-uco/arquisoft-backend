package com.arquisoft.biblioteca.application.documentacion.query.secondaryport;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface DocumentacionQueryOutputPort {

    PaginatedResult<DocumentacionReadModel> consultar(DocumentacionCriteria criteria);
}

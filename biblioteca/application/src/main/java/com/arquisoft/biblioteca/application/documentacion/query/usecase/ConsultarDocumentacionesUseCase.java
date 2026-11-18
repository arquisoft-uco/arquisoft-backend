package com.arquisoft.biblioteca.application.documentacion.query.usecase;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarDocumentacionesUseCase
        extends UseCase<DocumentacionCriteria, PaginatedResult<DocumentacionReadModel>> {}

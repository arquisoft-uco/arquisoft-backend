package com.arquisoft.biblioteca.application.documentacion.query.usecase;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDocumentacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarEnlaceDocumentacionUseCase
        extends UseCase<EnlaceDocumentacionCriteria, EnlaceDocumentacionReadModel> {}

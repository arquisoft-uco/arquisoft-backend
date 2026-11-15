package com.arquisoft.biblioteca.application.documentacion.query.usecase;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

public interface DescargarDocumentacionUseCase
        extends UseCase<DescargaDocumentacionCriteria, EnlaceDescargaDocumentacionReadModel> {}

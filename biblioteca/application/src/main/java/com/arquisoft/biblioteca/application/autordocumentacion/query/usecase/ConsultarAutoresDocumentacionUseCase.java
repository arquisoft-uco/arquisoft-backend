package com.arquisoft.biblioteca.application.autordocumentacion.query.usecase;

import com.arquisoft.biblioteca.application.autordocumentacion.query.criteria.AutorDocumentacionCriteria;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.List;

public interface ConsultarAutoresDocumentacionUseCase
        extends UseCase<AutorDocumentacionCriteria, List<AutorDocumentacionReadModel>> {}

package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor;

import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;
import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.List;

public interface ConsultarAutoresDocumentacionInteractor
        extends Interactor<ConsultarAutoresDocumentacionQuery, List<AutorDocumentacionReadModel>> {}

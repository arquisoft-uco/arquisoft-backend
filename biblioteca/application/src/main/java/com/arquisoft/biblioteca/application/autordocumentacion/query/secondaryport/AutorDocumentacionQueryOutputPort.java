package com.arquisoft.biblioteca.application.autordocumentacion.query.secondaryport;

import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;

import java.util.List;
import java.util.UUID;

public interface AutorDocumentacionQueryOutputPort {

    List<AutorDocumentacionReadModel> consultarPorDocumentacion(UUID documentacion);
}

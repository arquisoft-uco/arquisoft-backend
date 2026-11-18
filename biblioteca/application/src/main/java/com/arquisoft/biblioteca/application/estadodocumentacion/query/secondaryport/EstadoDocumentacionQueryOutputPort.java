package com.arquisoft.biblioteca.application.estadodocumentacion.query.secondaryport;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;

import java.util.List;

public interface EstadoDocumentacionQueryOutputPort {

    List<EstadoDocumentacionReadModel> findAll();
}

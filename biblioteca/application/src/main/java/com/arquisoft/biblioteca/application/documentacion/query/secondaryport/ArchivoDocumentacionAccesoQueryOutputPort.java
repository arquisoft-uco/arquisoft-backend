package com.arquisoft.biblioteca.application.documentacion.query.secondaryport;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity.ArchivoDocumentacionAccesoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArchivoDocumentacionAccesoQueryOutputPort {

    Optional<ArchivoDocumentacionAccesoEntity> obtenerPorId(UUID documentacion);
}

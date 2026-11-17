package com.arquisoft.biblioteca.application.documentacion.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;

public final class CambiarEstadoDocumentacionMapper {

    private CambiarEstadoDocumentacionMapper() {}

    public static CambioEstadoDocumentacionDomain toDomain(CambiarEstadoDocumentacionCommand command) {
        return CambioEstadoDocumentacionDomain.crear(command.documentacion(), command.estado());
    }
}

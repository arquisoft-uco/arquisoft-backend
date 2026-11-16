package com.arquisoft.biblioteca.application.documentacion.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.RegistrarDocumentacionCommand;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;

public final class RegistrarDocumentacionMapper {

    private RegistrarDocumentacionMapper() {}

    public static RegistroDocumentacionDomain toDomain(RegistrarDocumentacionCommand command) {
        return RegistroDocumentacionDomain.crear(command.entregableProyectoGrado(), command.bibliotecario());
    }
}

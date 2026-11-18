package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.RegistrarDocumentacionCommand;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.RegistrarDocumentacionRequestDTO;

import java.util.UUID;

public final class RegistrarDocumentacionRequestMapper {

    private RegistrarDocumentacionRequestMapper() {}

    public static RegistrarDocumentacionCommand toCommand(RegistrarDocumentacionRequestDTO dto, UUID bibliotecario) {
        return RegistrarDocumentacionCommand.crear(dto.entregableProyectoGradoId(), bibliotecario);
    }
}

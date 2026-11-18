package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.model.CambiarEstadoDocumentacionCommand;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.CambiarEstadoDocumentacionRequestDTO;

import java.util.UUID;

public final class CambiarEstadoDocumentacionRequestMapper {

    private CambiarEstadoDocumentacionRequestMapper() {}

    public static CambiarEstadoDocumentacionCommand toCommand(UUID documentacionId,
                                                              CambiarEstadoDocumentacionRequestDTO dto) {
        return CambiarEstadoDocumentacionCommand.crear(documentacionId, dto.estado());
    }
}

package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web.dto.EstadoDocumentacionResponseDTO;

public final class EstadoDocumentacionResponseMapper {

    private EstadoDocumentacionResponseMapper() {}

    public static EstadoDocumentacionResponseDTO toResponse(EstadoDocumentacionReadModel readModel) {
        return new EstadoDocumentacionResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}

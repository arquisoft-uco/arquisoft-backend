package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.dto.AutorDocumentacionResponseDTO;

public final class AutorDocumentacionResponseMapper {

    private AutorDocumentacionResponseMapper() {}

    public static AutorDocumentacionResponseDTO toResponse(AutorDocumentacionReadModel readModel) {
        return new AutorDocumentacionResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email());
    }
}

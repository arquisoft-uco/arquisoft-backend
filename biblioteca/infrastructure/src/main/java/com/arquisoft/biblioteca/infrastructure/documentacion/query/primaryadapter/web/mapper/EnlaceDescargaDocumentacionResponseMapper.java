package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.EnlaceDescargaDocumentacionResponseDTO;

public final class EnlaceDescargaDocumentacionResponseMapper {

    private EnlaceDescargaDocumentacionResponseMapper() {}

    public static EnlaceDescargaDocumentacionResponseDTO toResponse(EnlaceDescargaDocumentacionReadModel readModel) {
        return new EnlaceDescargaDocumentacionResponseDTO(readModel.url(), readModel.expiraEn());
    }
}

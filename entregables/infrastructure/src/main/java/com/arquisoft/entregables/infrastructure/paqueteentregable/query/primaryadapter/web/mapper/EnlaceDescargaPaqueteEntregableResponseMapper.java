package com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.dto.EnlaceDescargaPaqueteEntregableResponseDTO;

public final class EnlaceDescargaPaqueteEntregableResponseMapper {

    private EnlaceDescargaPaqueteEntregableResponseMapper() {}

    public static EnlaceDescargaPaqueteEntregableResponseDTO toResponse(
            EnlaceDescargaPaqueteEntregableReadModel readModel) {
        return new EnlaceDescargaPaqueteEntregableResponseDTO(readModel.url(), readModel.expiraEn());
    }
}

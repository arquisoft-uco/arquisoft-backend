package com.arquisoft.usuarios.infrastructure.coordinador.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.coordinador.query.primaryadapter.web.dto.CoordinadorVigenteResponseDTO;

public final class CoordinadorVigenteResponseMapper {

    private CoordinadorVigenteResponseMapper() {}

    public static CoordinadorVigenteResponseDTO toResponse(CoordinadorVigenteReadModel readModel) {
        return new CoordinadorVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto());
    }
}

package com.arquisoft.usuarios.infrastructure.estudiante.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.estudiante.query.primaryadapter.web.dto.EstudianteVigenteResponseDTO;

public final class EstudianteVigenteResponseMapper {

    private EstudianteVigenteResponseMapper() {}

    public static EstudianteVigenteResponseDTO toResponse(EstudianteVigenteReadModel readModel) {
        return new EstudianteVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto());
    }
}

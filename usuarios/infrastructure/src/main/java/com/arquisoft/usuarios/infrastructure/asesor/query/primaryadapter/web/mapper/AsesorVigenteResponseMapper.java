package com.arquisoft.usuarios.infrastructure.asesor.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesor.query.primaryadapter.web.dto.AsesorVigenteResponseDTO;

public final class AsesorVigenteResponseMapper {

    private AsesorVigenteResponseMapper() {}

    public static AsesorVigenteResponseDTO toResponse(AsesorVigenteReadModel readModel) {
        return new AsesorVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado());
    }
}

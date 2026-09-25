package com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.dto.AsesorFichaVigenteResponseDTO;

public final class AsesorFichaVigenteResponseMapper {

    private AsesorFichaVigenteResponseMapper() {}

    public static AsesorFichaVigenteResponseDTO toResponse(AsesorFichaVigenteReadModel readModel) {
        return new AsesorFichaVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado());
    }
}

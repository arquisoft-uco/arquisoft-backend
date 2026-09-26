package com.arquisoft.usuarios.infrastructure.asesor.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.infrastructure.asesor.query.primaryadapter.web.dto.AsesorResponseDTO;

public final class AsesorResponseMapper {

    private AsesorResponseMapper() {}

    public static AsesorResponseDTO toResponse(AsesorReadModel readModel) {
        return new AsesorResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}

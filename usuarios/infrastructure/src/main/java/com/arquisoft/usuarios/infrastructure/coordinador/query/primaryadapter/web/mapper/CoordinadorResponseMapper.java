package com.arquisoft.usuarios.infrastructure.coordinador.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.infrastructure.coordinador.query.primaryadapter.web.dto.CoordinadorResponseDTO;

public final class CoordinadorResponseMapper {

    private CoordinadorResponseMapper() {}

    public static CoordinadorResponseDTO toResponse(CoordinadorReadModel readModel) {
        return new CoordinadorResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}

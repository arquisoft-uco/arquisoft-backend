package com.arquisoft.usuarios.infrastructure.representantecomite.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteResponseDTO;

public final class RepresentanteComiteResponseMapper {

    private RepresentanteComiteResponseMapper() {}

    public static RepresentanteComiteResponseDTO toResponse(RepresentanteComiteReadModel readModel) {
        return new RepresentanteComiteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}

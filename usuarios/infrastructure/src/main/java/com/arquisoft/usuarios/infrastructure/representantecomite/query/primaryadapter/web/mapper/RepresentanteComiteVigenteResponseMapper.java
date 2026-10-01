package com.arquisoft.usuarios.infrastructure.representantecomite.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.representantecomite.query.primaryadapter.web.dto.RepresentanteComiteVigenteResponseDTO;

public final class RepresentanteComiteVigenteResponseMapper {

    private RepresentanteComiteVigenteResponseMapper() {}

    public static RepresentanteComiteVigenteResponseDTO toResponse(RepresentanteComiteVigenteReadModel readModel) {
        return new RepresentanteComiteVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado());
    }
}

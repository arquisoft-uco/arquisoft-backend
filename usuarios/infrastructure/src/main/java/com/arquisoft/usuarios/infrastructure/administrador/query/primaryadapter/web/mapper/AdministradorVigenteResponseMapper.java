package com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web.dto.AdministradorVigenteResponseDTO;

public final class AdministradorVigenteResponseMapper {

    private AdministradorVigenteResponseMapper() {}

    public static AdministradorVigenteResponseDTO toResponse(AdministradorVigenteReadModel readModel) {
        return new AdministradorVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado());
    }
}

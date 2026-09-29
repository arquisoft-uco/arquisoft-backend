package com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web.dto.AdministradorResponseDTO;

public final class AdministradorResponseMapper {

    private AdministradorResponseMapper() {}

    public static AdministradorResponseDTO toResponse(AdministradorReadModel readModel) {
        return new AdministradorResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}

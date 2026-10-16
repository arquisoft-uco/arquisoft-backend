package com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web.dto.JuradoVigenteResponseDTO;

public final class JuradoVigenteResponseMapper {

    private JuradoVigenteResponseMapper() {}

    public static JuradoVigenteResponseDTO toResponse(JuradoVigenteReadModel readModel) {
        return new JuradoVigenteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado());
    }
}

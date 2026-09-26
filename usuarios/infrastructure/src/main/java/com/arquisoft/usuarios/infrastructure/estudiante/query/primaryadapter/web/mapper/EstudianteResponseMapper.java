package com.arquisoft.usuarios.infrastructure.estudiante.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.usuarios.infrastructure.estudiante.query.primaryadapter.web.dto.EstudianteResponseDTO;

public final class EstudianteResponseMapper {

    private EstudianteResponseMapper() {}

    public static EstudianteResponseDTO toResponse(EstudianteReadModel readModel) {
        return new EstudianteResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                readModel.estado(),
                readModel.vigente());
    }
}

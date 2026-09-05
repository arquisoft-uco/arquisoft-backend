package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.dto.EstudianteFichaPerfilResponseDTO;

public final class EstudianteFichaPerfilResponseMapper {

    private EstudianteFichaPerfilResponseMapper() {}

    public static EstudianteFichaPerfilResponseDTO toResponse(EstudianteFichaPerfilReadModel readModel) {
        return new EstudianteFichaPerfilResponseDTO(
                readModel.id(),
                readModel.fichaPerfilId(),
                readModel.estudianteId(),
                readModel.nombre(),
                readModel.email());
    }
}

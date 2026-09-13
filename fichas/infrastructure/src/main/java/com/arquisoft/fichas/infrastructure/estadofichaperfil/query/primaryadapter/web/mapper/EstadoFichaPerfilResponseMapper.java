package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.dto.EstadoFichaPerfilResponseDTO;

public final class EstadoFichaPerfilResponseMapper {

    private EstadoFichaPerfilResponseMapper() {}

    public static EstadoFichaPerfilResponseDTO toResponse(EstadoFichaPerfilReadModel readModel) {
        return new EstadoFichaPerfilResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.fechaActualizacion());
    }
}
